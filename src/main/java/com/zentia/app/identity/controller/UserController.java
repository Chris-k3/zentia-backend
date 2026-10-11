package com.zentia.app.identity.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
}
