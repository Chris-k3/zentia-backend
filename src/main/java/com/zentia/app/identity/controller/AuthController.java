package com.zentia.app.identity.controller;

import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.zentia.app.identity.dto.AuthResponseDTO;
import com.zentia.app.identity.dto.GoogleAuthRequestDTO;
import com.zentia.app.identity.services.UserService;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    /**
     * POST /api/v1/auth/google
     * Registra o autentica al usuario mediante las credenciales de Google OAuth 2.0 y retorna el JWT.
     */
    @PostMapping("/google")
    public ResponseEntity<?> authenticateWithGoogle(@RequestBody GoogleAuthRequestDTO request) {
        try {
            AuthResponseDTO response = userService.authenticateWithGoogle(request);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        }
    }
}

