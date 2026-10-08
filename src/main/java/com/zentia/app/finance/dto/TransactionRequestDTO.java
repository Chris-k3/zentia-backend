package com.zentia.app.finance.dto;

import java.math.BigDecimal;

//Esta clase atrapa el json que llega del front y lo convierte en un objeto java para poder trabajar con el
public class TransactionRequestDTO {
    //Solo pedimos dis para conectar la transaccion con el usuario, el resto de los datos se obtienen del front
    private Long userId;
    private Long categoryId;

    //BigDecimal para las reglas del negocio
    private BigDecimal amount;
    private String type;
    private String description;
    private String transactionDate;

    public TransactionRequestDTO() {
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(String transactionDate) {
        this.transactionDate = transactionDate;
    }

    //getters and setters
    
}
