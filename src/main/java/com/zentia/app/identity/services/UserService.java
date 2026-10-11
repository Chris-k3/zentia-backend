package com.zentia.app.identity.services;

import org.springframework.stereotype.Service;

import com.zentia.app.identity.dto.UserResponseDTO;
import com.zentia.app.identity.model.User;
import com.zentia.app.identity.repository.UserRepository;
import com.zentia.app.identity.security.JwtUtil;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    // Inyección de dependencias por constructor
    public UserService(UserRepository userRepository, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
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
}
