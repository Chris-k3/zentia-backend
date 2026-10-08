package com.zentia.app.finance.port;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.zentia.app.finance.model.Transaction;

//Es el puente entre la capa de dominio y la capa de infraestructura,
// permitiendo que la lógica de negocio interactúe con 
// la base de datos sin depender directamente de la implementación 
// específica del repositorio.
public interface TransactionRepositoryPort {
    //El sistema necesita guardar transacciones en la base de datos, y este método define la operación para hacerlo.
    Transaction save(Transaction transaction);
    List<Transaction> findByUserIdAndTransactionDateBetween(Long userId, LocalDateTime startDate, LocalDateTime endDate);
    //SELECT * FROM transaction WHERE user_id = ? AND transaction_date BETWEEN ? AND ?
    //List = una colección que puede contener múltiples elementos, en este caso, 
    // transacciones asociadas a un usuario específico.
}
