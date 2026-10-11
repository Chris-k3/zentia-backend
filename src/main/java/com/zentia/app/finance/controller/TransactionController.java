package com.zentia.app.finance.controller;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import com.zentia.app.finance.dto.TransactionRequestDTO;
import com.zentia.app.finance.dto.TransactionResponseDTO;
import com.zentia.app.finance.model.Transaction;
import com.zentia.app.finance.usecase.*;

@RestController
@RequestMapping("/api/v1/transactions")
public class TransactionController {

    private final RegistrarTransaccionUseCase registrarUseCase;
    private final ConsultarHistorialUseCase consultarHistorialUseCase;
    private final ActualizarTransaccionUseCase actualizarUseCase;
    private final EliminarTransaccionUseCase eliminarUseCase;
    private final GenerarResumenDashboardUseCase resumenUseCase;

    public TransactionController(
            RegistrarTransaccionUseCase registrarUseCase,
            ConsultarHistorialUseCase consultarHistorialUseCase,
            ActualizarTransaccionUseCase actualizarUseCase,
            EliminarTransaccionUseCase eliminarUseCase,
            GenerarResumenDashboardUseCase resumenUseCase) {
        this.registrarUseCase = registrarUseCase;
        this.consultarHistorialUseCase = consultarHistorialUseCase;
        this.actualizarUseCase = actualizarUseCase;
        this.eliminarUseCase = eliminarUseCase;
        this.resumenUseCase = resumenUseCase;
    }

    /**
     * POST /api/v1/transactions
     * El userId se obtiene del Token JWT autenticado para prevenir ataques IDOR.
     */
    @PostMapping
    public ResponseEntity<TransactionResponseDTO> registrar(
            @AuthenticationPrincipal Long authUserId,
            @RequestBody TransactionRequestDTO request) {

        Long userId = resolveUserId(authUserId);

        LocalDate date = request.getTransactionDate() != null 
                ? LocalDate.parse(request.getTransactionDate().toString()) 
                : LocalDate.now();

        Transaction transaction = registrarUseCase.ejecutar(
            userId,
            request.getCategoryId(),
            request.getAmount(),
            request.getType(),
            request.getDescription(),
            date
        );
        return ResponseEntity.ok(mapToResponse(transaction));
    }

    /**
     * GET /api/v1/transactions/historial?startDate=YYYY-MM-DD&endDate=YYYY-MM-DD
     * Consulta el historial del usuario autenticado sin recibir userId en la ruta.
     */
    @GetMapping("/historial")
    public ResponseEntity<List<TransactionResponseDTO>> consultarHistorial(
            @AuthenticationPrincipal Long authUserId,
            @RequestParam String startDate,
            @RequestParam String endDate) {

        Long userId = resolveUserId(authUserId);

        List<Transaction> transactions = consultarHistorialUseCase.ejecutar(
            userId, LocalDate.parse(startDate), LocalDate.parse(endDate)
        );

        List<TransactionResponseDTO> response = transactions.stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    /**
     * PUT /api/v1/transactions/{transactionId}
     * Actualiza la transacción verificando que pertenezca al usuario autenticado.
     */
    @PutMapping("/{transactionId}")
    public ResponseEntity<TransactionResponseDTO> actualizar(
            @PathVariable Long transactionId,
            @AuthenticationPrincipal Long authUserId,
            @RequestBody TransactionRequestDTO request) {

        Long userId = resolveUserId(authUserId);

        Transaction updated = actualizarUseCase.ejecutar(
            userId,
            transactionId,
            request.getAmount(),
            request.getCategoryId(),
            request.getDescription()
        );

        return ResponseEntity.ok(mapToResponse(updated));
    }

    /**
     * DELETE /api/v1/transactions/{transactionId}
     * Elimina la transacción del usuario autenticado sin requerir userId como parámetro.
     */
    @DeleteMapping("/{transactionId}")
    public ResponseEntity<Void> eliminar(
            @PathVariable Long transactionId,
            @AuthenticationPrincipal Long authUserId) {

        Long userId = resolveUserId(authUserId);

        eliminarUseCase.ejecutar(userId, transactionId);
        return ResponseEntity.noContent().build();
    }

    /**
     * GET /api/v1/transactions/dashboard?startDate=YYYY-MM-DD&endDate=YYYY-MM-DD
     * Obtiene el resumen financiero únicamente del usuario autenticado.
     */
    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, BigDecimal>> obtenerResumen(
            @AuthenticationPrincipal Long authUserId,
            @RequestParam String startDate,
            @RequestParam String endDate) {

        Long userId = resolveUserId(authUserId);

        Map<String, BigDecimal> resumen = resumenUseCase.ejecutar(
            userId, LocalDate.parse(startDate), LocalDate.parse(endDate)
        );

        return ResponseEntity.ok(resumen);
    }

    /**
     * Resuelve el ID del usuario autenticado desde el contexto de Spring Security.
     */
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

    private TransactionResponseDTO mapToResponse(Transaction t) {
        String categoryName = t.getCategory() != null ? t.getCategory().getName() : "";
        String dateString = t.getTransactionDate() != null ? t.getTransactionDate().toString() : "";
        double amount = t.getAmount() != null ? t.getAmount().doubleValue() : 0.0;
        String typeString = t.getType() != null ? t.getType().name() : "";
        return new TransactionResponseDTO(
            t.getId() != null ? t.getId() : 0L,
            t.getDescription(),
            amount,
            dateString,
            typeString,
            categoryName
        );
    }
}
