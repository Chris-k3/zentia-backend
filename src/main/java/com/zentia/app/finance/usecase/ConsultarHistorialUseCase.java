package com.zentia.app.finance.usecase;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.zentia.app.finance.model.Transaction;
import com.zentia.app.finance.port.TransactionRepositoryPort;
import com.zentia.app.identity.services.UserService;

@Service 
public class ConsultarHistorialUseCase {
    private final TransactionRepositoryPort transactionRepositoryPort;
    private final UserService userService; 

    public ConsultarHistorialUseCase(TransactionRepositoryPort transactionRepositoryPort, UserService userService) {
        this.transactionRepositoryPort = transactionRepositoryPort;
        this.userService = userService;
    } 

    public List<Transaction> ejecutar(Long userId, LocalDate startDate, LocalDate endDate){
        // Regla 1: Validar que el usuario exista
        userService.findUserById(userId);

        // Buscar a través de la abstracción (Puerto) enviando directamente LocalDate
        return transactionRepositoryPort.findByUserIdAndTransactionDateBetween(userId, startDate, endDate);
    }
}

