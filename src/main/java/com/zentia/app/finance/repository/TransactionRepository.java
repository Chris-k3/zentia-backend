package com.zentia.app.finance.repository;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

import com.zentia.app.finance.model.Transaction;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByUserId(Long userId);//SELECT * FROM transaction WHERE user_id = ?

    List<Transaction> findByUserIdAndTransactionDateBetween(Long userId, LocalDate startDate, LocalDate endDate);
    //SELECT * FROM transaction WHERE user_id = ? AND transaction_date BETWEEN ? AND ?
    //List = una colección que puede contener múltiples elementos, en este caso, 
    // transacciones asociadas a un usuario específico.

}
