package com.zentia.app.finance.adapter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import com.zentia.app.finance.model.Transaction;
import com.zentia.app.finance.port.TransactionRepositoryPort;

public class InMemoryTransactionRepositoryAdapter implements TransactionRepositoryPort {

    private final Map<Long, Transaction> storage = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    @Override
    public Transaction save(Transaction transaction) {
        if (transaction.getId() == null) {
            transaction.setId(idGenerator.getAndIncrement());
        }
        storage.put(transaction.getId(), transaction);
        return transaction;
    }

    @Override
    public List<Transaction> findByUserIdAndTransactionDateBetween(Long userId, LocalDate startDate, LocalDate endDate) {
        List<Transaction> result = new ArrayList<>();
        for (Transaction transaction : storage.values()) {
            if (transaction.getUser() != null && userId.equals(transaction.getUser().getId())) {
                LocalDate date = transaction.getTransactionDate();
                if (date != null && !date.isBefore(startDate) && !date.isAfter(endDate)) {
                    result.add(transaction);
                }
            }
        }
        return result;
    }

    @Override
    public List<Transaction> findByUserId(Long userId) {
        List<Transaction> result = new ArrayList<>();
        for (Transaction transaction : storage.values()) {
            if (transaction.getUser() != null && userId.equals(transaction.getUser().getId())) {
                result.add(transaction);
            }
        }
        return result;
    }

    @Override
    public Optional<Transaction> findById(Long id) {
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public void deleteById(Long id) {
        storage.remove(id);
    }

    public void clear() {
        storage.clear();
        idGenerator.set(1);
    }

    public int count() {
        return storage.size();
    }
}
