package com.zentia.app.identity.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import com.zentia.app.identity.dto.FinancialProfileRequestDTO;
import com.zentia.app.identity.dto.FinancialProfileResponseDTO;
import com.zentia.app.identity.dto.UserResponseDTO;
import com.zentia.app.identity.services.UserService;

import java.util.Map;

@RestController
@RequestMapping("/api/v1")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Endpoint para solicitar código OTP para inicio de sesión de respaldo.
     */
    @PostMapping("/auth/otp/solicitar")
    public ResponseEntity<Map<String, String>> solicitarOtp(@RequestParam String phoneNumber) {
        try {
            userService.solicitarCodigoOtp(phoneNumber);
            return ResponseEntity.ok(Map.of("message", "Código OTP enviado exitosamente a " + phoneNumber));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Endpoint para verificar el código OTP y recibir el Token JWT de sesión.
     */
    @PostMapping("/auth/otp/verificar")
    public ResponseEntity<Map<String, String>> verificarOtp(
            @RequestParam String phoneNumber,
            @RequestParam String codigo) {
        try {
            String token = userService.verificarCodigoOtp(phoneNumber, codigo);
            return ResponseEntity.ok(Map.of(
                    "message", "Login exitoso",
                    "token", token
            ));
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Endpoint para vincular un número telefónico al perfil de un usuario existente.
     */
    @PutMapping("/users/profile/phone")
    public ResponseEntity<?> vincularTelefono(
            @RequestParam Long userId,
            @RequestParam String phoneNumber) {
        try {
            UserResponseDTO updatedUser = userService.vincularTelefono(userId, phoneNumber);
            return ResponseEntity.ok(updatedUser);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * POST /api/v1/users/onboarding (y PUT /api/v1/users/financial-profile)
     * Guarda el perfil financiero del usuario autenticado y actualiza automáticamente onboardingCompleted = true.
     */
    @RequestMapping(
            value = {"/users/onboarding", "/users/financial-profile"},
            method = {RequestMethod.POST, RequestMethod.PUT}
    )
    public ResponseEntity<?> completarOnboarding(
            @AuthenticationPrincipal Long authUserId,
            @RequestBody FinancialProfileRequestDTO request) {
        try {
            Long userId = resolveUserId(authUserId);
            FinancialProfileResponseDTO response = userService.completarOnboarding(userId, request);
            return ResponseEntity.ok(response);
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * GET /api/v1/users/financial-profile
     * Devuelve el ingreso mensual estimado, la meta de ahorro y la categoría prioritaria del usuario autenticado.
     */
    @GetMapping("/users/financial-profile")
    public ResponseEntity<?> obtenerPerfilFinanciero(@AuthenticationPrincipal Long authUserId) {
        try {
            Long userId = resolveUserId(authUserId);
            FinancialProfileResponseDTO response = userService.obtenerPerfilFinanciero(userId);
            return ResponseEntity.ok(response);
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }
    }

    private Long resolveUserId(Long authUserId) {
        if (authUserId != null) {
            return authUserId;
        }
        Object principal = SecurityContextHolder.getContext().getAuthentication() != null
                ? SecurityContextHolder.getContext().getAuthentication().getPrincipal()
                : null;

        if (principal instanceof Long) {
            return (Long) principal;
        }
        if (principal instanceof String) {
            return Long.parseLong((String) principal);
        }
        throw new SecurityException("Usuario no autenticado o token JWT inválido.");
    }
}
