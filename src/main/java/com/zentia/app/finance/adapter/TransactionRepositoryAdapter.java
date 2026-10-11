package com.zentia.app.finance.adapter;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Component;
import com.zentia.app.finance.model.Transaction;
import com.zentia.app.finance.port.TransactionRepositoryPort;
import com.zentia.app.finance.repository.TransactionRepository;
@Component 
//La anotación @Component indica que esta clase es un componente de Spring, 
// lo que permite que Spring la detecte y la gestione automáticamente como un bean.

//La clase TransactionRepositoryAdapter implementa la interfaz TransactionRepositoryPort,
// actuando como un adaptador que conecta la capa de dominio con la capa de infraestructura.
//basicamente le decimos a Spring eres un componente e implementas la funcion de 
//de poder gua
public class TransactionRepositoryAdapter implements TransactionRepositoryPort {
    private final TransactionRepository transactionRepository;

    public TransactionRepositoryAdapter(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    @Override
    public Transaction save(Transaction transaction) {
        return transactionRepository.save(transaction);
    }

    @Override
    public List<Transaction> findByUserIdAndTransactionDateBetween(Long userId, java.time.LocalDate startDate, java.time.LocalDate endDate) {
        return transactionRepository.findByUserIdAndTransactionDateBetween(userId, startDate, endDate);
    }

    @Override
    public List<Transaction> findByUserId(Long userId) {
        return transactionRepository.findByUserId(userId);
    }

    @Override
    public java.util.Optional<Transaction> findById(Long id) {
        return transactionRepository.findById(id);
    }

    @Override
    public void deleteById(Long id) {
        transactionRepository.deleteById(id);
    }
}
