package com.zentia.app.finance.usecase;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.zentia.app.finance.model.Transaction;
import com.zentia.app.finance.model.TransactionType;
import com.zentia.app.finance.port.TransactionRepositoryPort;
import com.zentia.app.identity.services.UserService;

@Service
public class GenerarResumenDashboardUseCase {

    private final TransactionRepositoryPort transactionRepositoryPort;
    private final UserService userService;

    public GenerarResumenDashboardUseCase(TransactionRepositoryPort transactionRepositoryPort, UserService userService) {
        this.transactionRepositoryPort = transactionRepositoryPort;
        this.userService = userService;
    }

    public Map<String, BigDecimal> ejecutar(Long userId, LocalDate startDate, LocalDate endDate) {
        // Validar usuario
        userService.findUserById(userId);

        // Búsqueda directa usando LocalDate
        List<Transaction> transacciones = transactionRepositoryPort
            .findByUserIdAndTransactionDateBetween(userId, startDate, endDate);

        BigDecimal totalIngresos = BigDecimal.ZERO;
        BigDecimal totalGastos = BigDecimal.ZERO;

        for (Transaction t : transacciones) {
            if (t.getType() == TransactionType.INGRESO) {
                totalIngresos = totalIngresos.add(t.getAmount());
            } else if (t.getType() == TransactionType.GASTO) {
                totalGastos = totalGastos.add(t.getAmount());
            }
        }

        BigDecimal balanceActual = totalIngresos.subtract(totalGastos);

        return Map.of(
            "totalIngresos", totalIngresos,
            "totalGastos", totalGastos,
            "balanceActual", balanceActual
        );
    }
}

