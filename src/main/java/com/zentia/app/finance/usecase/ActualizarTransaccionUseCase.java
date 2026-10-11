package com.zentia.app.finance.usecase;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;

import com.zentia.app.finance.model.Category;
import com.zentia.app.finance.model.Transaction;
import com.zentia.app.finance.port.TransactionRepositoryPort;
import com.zentia.app.finance.services.CategoryService;

@Service
public class ActualizarTransaccionUseCase {

    private final TransactionRepositoryPort transactionRepositoryPort;
    private final CategoryService categoryService;

    public ActualizarTransaccionUseCase(TransactionRepositoryPort transactionRepositoryPort, CategoryService categoryService) {
        this.transactionRepositoryPort = transactionRepositoryPort;
        this.categoryService = categoryService;
    }

    public Transaction ejecutar(Long userId, Long transactionId, BigDecimal amount, Long categoryId, String description) {
        // 1. Buscar la transacción original
        Transaction transaction = transactionRepositoryPort.findById(transactionId)
            .orElseThrow(() -> new IllegalArgumentException("La transacción no existe."));

        // 2. Validar propiedad estricta (Autorización)
        if (!transaction.getUser().getId().equals(userId)) {
            throw new SecurityException("No tienes permiso para modificar esta transacción.");
        }

        // 3. Aplicar Regla Financiera ADR-005
        if (amount != null) {
            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("El monto debe ser estrictamente mayor a cero.");
            }
            transaction.setAmount(amount);
        }

        // 4. Actualizar categoría si se envía
        if (categoryId != null) {
            Category category = categoryService.findCategoryById(categoryId);
            transaction.setCategory(category);
        }

        // 5. Actualizar descripción
        if (description != null && !description.isBlank()) {
            transaction.setDescription(description);
        }

        return transactionRepositoryPort.save(transaction);
    }
}

