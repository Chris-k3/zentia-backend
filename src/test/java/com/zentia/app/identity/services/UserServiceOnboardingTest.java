package com.zentia.app.identity.services;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.zentia.app.finance.model.Category;
import com.zentia.app.finance.model.TransactionType;
import com.zentia.app.finance.repository.CategoryRepository;
import com.zentia.app.identity.dto.AuthResponseDTO;
import com.zentia.app.identity.dto.FinancialProfileRequestDTO;
import com.zentia.app.identity.dto.FinancialProfileResponseDTO;
import com.zentia.app.identity.dto.GoogleAuthRequestDTO;
import com.zentia.app.identity.model.User;
import com.zentia.app.identity.repository.UserRepository;
import com.zentia.app.identity.security.JwtUtil;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class UserServiceOnboardingTest {

    private UserRepository userRepository;
    private CategoryRepository categoryRepository;
    private JwtUtil jwtUtil;
    private UserService userService;

    @BeforeEach
    void setUp() {
        userRepository = Mockito.mock(UserRepository.class);
        categoryRepository = Mockito.mock(CategoryRepository.class);
        jwtUtil = new JwtUtil();
        userService = new UserService(userRepository, jwtUtil, categoryRepository);
    }

    @Test
    @DisplayName("Debe guardar el perfil financiero y marcar onboardingCompleted = true")
    void debeCompletarOnboardingYActualizarEstado() {
        User user = new User();
        user.setId(1L);
        user.setEmail("chris@zentia.com");
        user.setName("Chris");
        user.setOnboardingCompleted(false);

        Category category = new Category();
        category.setId(1L);
        category.setName("Alimentación");
        category.setType(TransactionType.GASTO);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        FinancialProfileRequestDTO request = new FinancialProfileRequestDTO(
                new BigDecimal("25000.00"),
                new BigDecimal("5000.00"),
                new BigDecimal("15000.00"),
                1L
        );

        FinancialProfileResponseDTO response = userService.completarOnboarding(1L, request);

        assertThat(response.isOnboardingCompleted()).isTrue();
        assertThat(response.getMonthlyIncome()).isEqualByComparingTo("25000.00");
        assertThat(response.getMonthlySavingsGoal()).isEqualByComparingTo("5000.00");
        assertThat(response.getEstimatedMonthlyExpense()).isEqualByComparingTo("15000.00");
        assertThat(response.getPriorityCategoryId()).isEqualTo(1L);
        assertThat(response.getPriorityCategoryName()).isEqualTo("Alimentación");
        assertThat(user.isOnboardingCompleted()).isTrue();
    }

    @Test
    @DisplayName("Debe consultar el perfil financiero del usuario autenticado")
    void debeObtenerPerfilFinanciero() {
        Category category = new Category();
        category.setId(2L);
        category.setName("Transporte");

        User user = new User();
        user.setId(1L);
        user.setMonthlyIncome(new BigDecimal("25000.00"));
        user.setMonthlySavingsGoal(new BigDecimal("5000.00"));
        user.setPriorityCategory(category);
        user.setOnboardingCompleted(true);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        FinancialProfileResponseDTO profile = userService.obtenerPerfilFinanciero(1L);

        assertThat(profile.getUserId()).isEqualTo(1L);
        assertThat(profile.getMonthlyIncome()).isEqualByComparingTo("25000.00");
        assertThat(profile.getMonthlySavingsGoal()).isEqualByComparingTo("5000.00");
        assertThat(profile.getPriorityCategoryId()).isEqualTo(2L);
        assertThat(profile.getPriorityCategoryName()).isEqualTo("Transporte");
        assertThat(profile.isOnboardingCompleted()).isTrue();
    }

    @Test
    @DisplayName("Debe rechazar ingreso mensual negativo o nulo")
    void debeRechazarIngresoInvalido() {
        FinancialProfileRequestDTO request = new FinancialProfileRequestDTO(
                new BigDecimal("-100.00"),
                new BigDecimal("5000.00"),
                null,
                null
        );

        assertThatThrownBy(() -> userService.completarOnboarding(1L, request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("monthlyIncome");
    }

    @Test
    @DisplayName("Debe autenticar con idToken de Google decodificando sub, email y name automáticamente y emitir JWT válido")
    void debeAutenticarSoloConIdTokenDeGoogle() {
        String header = Base64.getUrlEncoder().withoutPadding()
                .encodeToString("{\"alg\":\"RS256\"}".getBytes(StandardCharsets.UTF_8));
        String payload = Base64.getUrlEncoder().withoutPadding()
                .encodeToString("{\"sub\":\"google-999\",\"email\":\"front@zentia.com\",\"name\":\"Compañero Front\",\"picture\":\"https://img.url/pic.png\"}"
                        .getBytes(StandardCharsets.UTF_8));
        String fakeGoogleIdToken = header + "." + payload + ".sig";

        when(userRepository.findByGoogleId("google-999")).thenReturn(Optional.empty());
        when(userRepository.findByEmail("front@zentia.com")).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenAnswer(inv -> {
            User u = inv.getArgument(0);
            u.setId(42L);
            return u;
        });

        GoogleAuthRequestDTO dto = new GoogleAuthRequestDTO();
        dto.setIdToken(fakeGoogleIdToken);

        AuthResponseDTO authResponse = userService.authenticateWithGoogle(dto);

        assertThat(authResponse.getUserId()).isEqualTo(42L);
        assertThat(authResponse.getEmail()).isEqualTo("front@zentia.com");
        assertThat(authResponse.getName()).isEqualTo("Compañero Front");
        assertThat(authResponse.isOnboardingCompleted()).isFalse();
        assertThat(authResponse.isNewUser()).isTrue();
        assertThat(jwtUtil.validateToken(authResponse.getToken())).isTrue();
        assertThat(jwtUtil.extractUserId(authResponse.getToken())).isEqualTo(42L);
    }
}

