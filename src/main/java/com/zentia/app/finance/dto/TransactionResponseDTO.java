package com.zentia.app.finance.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.zentia.app.finance.model.Transaction;
import com.zentia.app.finance.model.TransactionType;

public class TransactionResponseDTO {

    private Long id;
    private BigDecimal amount;
    private TransactionType type;
    private String description;
    private LocalDateTime transactionDate;
    private Long categoryId;
    private String categoryName;
    private Long userId;
    private LocalDateTime createdAt;

    public TransactionResponseDTO() {
    }

    public TransactionResponseDTO(Long id, BigDecimal amount, TransactionType type, String description, LocalDateTime transactionDate, Long categoryId, String categoryName, Long userId, LocalDateTime createdAt) {
        this.id = id;
        this.amount = amount;
        this.type = type;
        this.description = description;
        this.transactionDate = transactionDate;
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.userId = userId;
        this.createdAt = createdAt;
    }

    // Constructor que toma la transacción de la base de datos y arma el ticket de respuesta
    public TransactionResponseDTO(Transaction transaction) {
        this.id = transaction.getId();
        this.amount = transaction.getAmount();
        this.type = transaction.getType();
        this.description = transaction.getDescription();
        this.transactionDate = transaction.getTransactionDate();
        this.createdAt = transaction.getCreatedAt();
        
        if (transaction.getCategory() != null) {
            this.categoryId = transaction.getCategory().getId();
            this.categoryName = transaction.getCategory().getName();
        }
        
        if (transaction.getUser() != null) {
            this.userId = transaction.getUser().getId();
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public TransactionType getType() {
        return type;
    }

    public void setType(TransactionType type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(LocalDateTime transactionDate) {
        this.transactionDate = transactionDate;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}