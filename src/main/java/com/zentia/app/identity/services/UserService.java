package com.zentia.app.identity.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zentia.app.finance.model.Category;
import com.zentia.app.finance.repository.CategoryRepository;
import com.zentia.app.identity.dto.AuthResponseDTO;
import com.zentia.app.identity.dto.FinancialProfileRequestDTO;
import com.zentia.app.identity.dto.FinancialProfileResponseDTO;
import com.zentia.app.identity.dto.GoogleAuthRequestDTO;
import com.zentia.app.identity.dto.UserResponseDTO;
import com.zentia.app.identity.model.User;
import com.zentia.app.identity.repository.UserRepository;
import com.zentia.app.identity.security.JwtUtil;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final CategoryRepository categoryRepository;

    // Inyección de dependencias por constructor
    @Autowired
    public UserService(UserRepository userRepository, JwtUtil jwtUtil, CategoryRepository categoryRepository) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
        this.categoryRepository = categoryRepository;
    }

    public UserService(UserRepository userRepository, JwtUtil jwtUtil) {
        this(userRepository, jwtUtil, null);
    }

    /**
     * Autenticación principal con Google OAuth 2.0.
     * Si el usuario envía un idToken de Google y omite algún campo (googleId, email, name, pictureUrl),
     * se decodifica el payload del idToken para completarlos.
     * Si el usuario es nuevo, lo registra en la base de datos con onboardingCompleted = false.
     * Si ya existe, actualiza sus datos de perfil (nombre/foto) y genera el Token JWT de sesión.
     */
    public AuthResponseDTO authenticateWithGoogle(GoogleAuthRequestDTO dto) {
        if (dto == null) {
            throw new IllegalArgumentException("El cuerpo de la petición es requerido.");
        }

        // Si el frontend envía el idToken de Google (ej. credential de @react-oauth/google),
        // extraemos automáticamente los campos faltantes desde su payload Base64Url
        if (dto.getIdToken() != null && !dto.getIdToken().isBlank()) {
            populateFromGoogleIdTokenIfMissing(dto);
        }

        if (dto.getGoogleId() == null || dto.getGoogleId().isBlank()) {
            throw new IllegalArgumentException("El googleId es requerido (o un idToken de Google válido).");
        }
        if (dto.getEmail() == null || dto.getEmail().isBlank()) {
            throw new IllegalArgumentException("El email es requerido (o un idToken de Google válido).");
        }

        // 1. Buscar si ya existe por googleId o por email
        Optional<User> userOptional = userRepository.findByGoogleId(dto.getGoogleId());
        if (userOptional.isEmpty()) {
            userOptional = userRepository.findByEmail(dto.getEmail());
        }

        User user;
        boolean isNewUser;

        if (userOptional.isEmpty()) {
            // Primer registro en Zentia
            user = new User();
            user.setGoogleId(dto.getGoogleId());
            user.setEmail(dto.getEmail());
            user.setName(dto.getName() != null && !dto.getName().isBlank() ? dto.getName() : "Usuario");
            user.setPictureUrl(dto.getPictureUrl());
            user.setOnboardingCompleted(false);

            user = userRepository.save(user);
            isNewUser = true;
        } else {
            // Usuario recurrente: sincronizar datos del perfil de Google si vienen en la petición
            user = userOptional.get();
            if (dto.getName() != null && !dto.getName().isBlank()) {
                user.setName(dto.getName());
            }
            if (dto.getPictureUrl() != null && !dto.getPictureUrl().isBlank()) {
                user.setPictureUrl(dto.getPictureUrl());
            }
            if (user.getGoogleId() == null || user.getGoogleId().isBlank()) {
                user.setGoogleId(dto.getGoogleId());
            }
            user = userRepository.save(user);
            isNewUser = false;
        }

        // Generar el Token JWT interno de Zentia
        String token = jwtUtil.generateToken(user);

        return new AuthResponseDTO(
                token,
                user.getId(),
                user.getEmail(),
                user.getName(),
                user.isOnboardingCompleted(),
                isNewUser
        );
    }

    // Método para uso interno (usado por finanzas / validación de dominio)
    public User findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado con ID: " + userId));
    }

    // Método para uso externo / API
    public UserResponseDTO getUserById(Long userId) {
        User user = findUserById(userId);
        return new UserResponseDTO(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getPictureUrl(),
                user.isOnboardingCompleted(),
                user.getPhoneNumber()
        );
    }

    /**
     * Vincula un número telefónico a la cuenta de un usuario existente.
     * Valida que el teléfono no esté en uso por otra persona.
     */
    public UserResponseDTO vincularTelefono(Long userId, String phoneNumber) {
        if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("El número de teléfono no puede estar vacío.");
        }

        User user = findUserById(userId);

        // Validar que no pertenezca a otra cuenta
        userRepository.findByPhoneNumber(phoneNumber).ifPresent(existingUser -> {
            if (!existingUser.getId().equals(userId)) {
                throw new IllegalStateException("El número de teléfono ya está vinculado a otra cuenta.");
            }
        });

        user.setPhoneNumber(phoneNumber);
        User savedUser = userRepository.save(user);

        return new UserResponseDTO(
                savedUser.getId(),
                savedUser.getName(),
                savedUser.getEmail(),
                savedUser.getPictureUrl(),
                savedUser.isOnboardingCompleted(),
                savedUser.getPhoneNumber()
        );
    }

    // Almacén en memoria para códigos OTP temporales
    private final Map<String, String> otpStorage = new ConcurrentHashMap<>();

    /**
     * Solicita un código OTP para login de respaldo.
     * Verifica estrictamente que el teléfono ya exista en la base de datos.
     */
    public void solicitarCodigoOtp(String phoneNumber) {
        // Validar que el teléfono exista en la base de datos (login de respaldo)
        userRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new IllegalArgumentException("El número de teléfono no está registrado en ninguna cuenta."));

        // Generar código aleatorio de 6 dígitos
        String otp = String.format("%06d", new Random().nextInt(999999));
        otpStorage.put(phoneNumber, otp);

        // Simular envío externo por consola
        System.out.println("==================================================");
        System.out.println("Enviando SMS/WhatsApp a " + phoneNumber);
        System.out.println("Su código OTP de Zentia es: " + otp);
        System.out.println("==================================================");
    }

    /**
     * Valida el código OTP y, si es correcto, genera y retorna el Token JWT.
     */
    public String verificarCodigoOtp(String phoneNumber, String codigo) {
        String storedOtp = otpStorage.get(phoneNumber);

        if (storedOtp != null && storedOtp.equals(codigo)) {
            // Código consumido con éxito: lo removemos para evitar reutilización
            otpStorage.remove(phoneNumber);

            User user = userRepository.findByPhoneNumber(phoneNumber)
                    .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado para el teléfono proporcionado."));

            // Generar y devolver el gafete de seguridad JWT
            return jwtUtil.generateToken(user);
        }

        throw new SecurityException("Código OTP inválido o expirado.");
    }

    /**
     * Completa el onboarding del usuario autenticado o actualiza su perfil financiero,
     * marcando automáticamente onboardingCompleted = true.
     */
    public FinancialProfileResponseDTO completarOnboarding(Long userId, FinancialProfileRequestDTO request) {
        if (request == null) {
            throw new IllegalArgumentException("Los datos del perfil financiero son requeridos.");
        }
        if (request.getMonthlyIncome() == null || request.getMonthlyIncome().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("El ingreso mensual estimado (monthlyIncome) es obligatorio y no puede ser negativo.");
        }
        if (request.getMonthlySavingsGoal() == null || request.getMonthlySavingsGoal().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("La meta de ahorro mensual (monthlySavingsGoal) es obligatoria y no puede ser negativa.");
        }
        if (request.getEstimatedMonthlyExpense() != null && request.getEstimatedMonthlyExpense().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("El gasto mensual estimado no puede ser negativo.");
        }

        User user = findUserById(userId);

        if (request.getPriorityCategoryId() != null) {
            if (categoryRepository == null) {
                throw new IllegalStateException("Repositorio de categorías no disponible.");
            }
            Category category = categoryRepository.findById(request.getPriorityCategoryId())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Categoría prioritaria no encontrada con ID: " + request.getPriorityCategoryId()));
            user.setPriorityCategory(category);
        } else {
            user.setPriorityCategory(null);
        }

        user.setMonthlyIncome(request.getMonthlyIncome());
        user.setMonthlySavingsGoal(request.getMonthlySavingsGoal());
        user.setEstimatedMonthlyExpense(request.getEstimatedMonthlyExpense());
        user.setOnboardingCompleted(true);

        User savedUser = userRepository.save(user);
        return mapToFinancialProfileResponse(savedUser);
    }

    /**
     * Consulta el perfil financiero del usuario autenticado para alimentar las barras de progreso del Dashboard.
     */
    public FinancialProfileResponseDTO obtenerPerfilFinanciero(Long userId) {
        User user = findUserById(userId);
        return mapToFinancialProfileResponse(user);
    }

    private FinancialProfileResponseDTO mapToFinancialProfileResponse(User user) {
        Long priorityCategoryId = user.getPriorityCategory() != null ? user.getPriorityCategory().getId() : null;
        String priorityCategoryName = user.getPriorityCategory() != null ? user.getPriorityCategory().getName() : null;

        return new FinancialProfileResponseDTO(
                user.getId(),
                user.getMonthlyIncome() != null ? user.getMonthlyIncome() : BigDecimal.ZERO,
                user.getMonthlySavingsGoal() != null ? user.getMonthlySavingsGoal() : BigDecimal.ZERO,
                user.getEstimatedMonthlyExpense(),
                priorityCategoryId,
                priorityCategoryName,
                user.isOnboardingCompleted()
        );
    }

    private void populateFromGoogleIdTokenIfMissing(GoogleAuthRequestDTO dto) {
        try {
            String[] parts = dto.getIdToken().split("\\.");
            if (parts.length < 2) {
                return;
            }
            String payloadJson = new String(
                    java.util.Base64.getUrlDecoder().decode(parts[1]),
                    java.nio.charset.StandardCharsets.UTF_8
            );
            if (dto.getGoogleId() == null || dto.getGoogleId().isBlank()) {
                dto.setGoogleId(extractJsonString(payloadJson, "sub"));
            }
            if (dto.getEmail() == null || dto.getEmail().isBlank()) {
                dto.setEmail(extractJsonString(payloadJson, "email"));
            }
            if (dto.getName() == null || dto.getName().isBlank()) {
                dto.setName(extractJsonString(payloadJson, "name"));
            }
            if (dto.getPictureUrl() == null || dto.getPictureUrl().isBlank()) {
                dto.setPictureUrl(extractJsonString(payloadJson, "picture"));
            }
        } catch (Exception ignored) {
            // Si el idToken no es un JWT Base64 válido, se validarán los campos explícitos normalmente
        }
    }

    private String extractJsonString(String json, String key) {
        Pattern pattern = Pattern.compile("\"" + Pattern.quote(key) + "\"\\s*:\\s*\"((?:\\\\.|[^\"\\\\])*)\"");
        Matcher matcher = pattern.matcher(json);
        if (!matcher.find()) {
            return null;
        }
        return matcher.group(1).replace("\\\"", "\"").replace("\\\\", "\\");
    }
}
