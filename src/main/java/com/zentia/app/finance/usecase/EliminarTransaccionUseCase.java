package com.zentia.app.finance.usecase;

import org.springframework.stereotype.Service;

import com.zentia.app.finance.model.Transaction;
import com.zentia.app.finance.port.TransactionRepositoryPort;

@Service
public class EliminarTransaccionUseCase {

    private final TransactionRepositoryPort transactionRepositoryPort;

    public EliminarTransaccionUseCase(TransactionRepositoryPort transactionRepositoryPort) {
        this.transactionRepositoryPort = transactionRepositoryPort;
    }

    public void ejecutar(Long userId, Long transactionId) {
        // 1. Buscar la transacción
        Transaction transaction = transactionRepositoryPort.findById(transactionId)
            .orElseThrow(() -> new IllegalArgumentException("La transacción no existe."));

        // 2. Validar propiedad estricta (Autorización)
        if (!transaction.getUser().getId().equals(userId)) {
            throw new SecurityException("No tienes permiso para eliminar esta transacción.");
        }

        // 3. Eliminar a través del puerto
        transactionRepositoryPort.deleteById(transactionId);
    }
}

