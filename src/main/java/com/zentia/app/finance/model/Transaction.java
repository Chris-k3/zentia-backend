package com.zentia.app.identity.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")
public class Transaction {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    //Relaciones con otras entidades en este caso, 
    //la transacción está relacionada con un usuario y una categoría.
    @ManyToOne (optional = false)//indica que la relación es obligatoria, es decir, una transacción debe estar asociada a un usuario y una categoría.
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    @ManyToOne (optional = false)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;
    @Column(nullable = false, precision = 19, scale = 2) //precision y scale definen la cantidad de dígitos totales y la cantidad de dígitos después del punto decimal, respectivamente.
    private BigDecimal amount;
    @Enumerated(EnumType.STRING) //esta anotacion indica que el valor del enum se almacenará como una cadena en la base de datos.
    private TransactionType type;
    @Column(length = 255)
    private String description;
    @Column(name = "transaction_date", nullable = false)
    private LocalDateTime transactionDate;//fecha de la transacción
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt; //cuando se creó la transacción

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    public Transaction() {
    }
    
    //getters and setters
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Category getCategory() {
		return category;
	}

	public void setCategory(Category category) {
		this.category = category;
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

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

    

}
