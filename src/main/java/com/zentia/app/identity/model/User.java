package com.zentia.app.identity.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import com.zentia.app.finance.model.Category;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(name = "google_id", nullable = false, unique = true, length = 100)
    private String googleId;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(name = "picture_url", length = 255)
    private String pictureUrl;

    @Column(name = "onboarding_completed", nullable = false)
    private boolean onboardingCompleted = false;

    @Column(name = "phone_number", unique = true, length = 20)
    private String phoneNumber;

    @Column(name = "monthly_income", precision = 19, scale = 2)
    private BigDecimal monthlyIncome;

    @Column(name = "monthly_savings_goal", precision = 19, scale = 2)
    private BigDecimal monthlySavingsGoal;

    @Column(name = "estimated_monthly_expense", precision = 19, scale = 2)
    private BigDecimal estimatedMonthlyExpense;

    @ManyToOne
    @JoinColumn(name = "priority_category_id")
    private Category priorityCategory;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    public User() {
    }



    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getGoogleId() {
        return googleId;
    }

    public void setGoogleId(String googleId) {
        this.googleId = googleId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isOnboardingCompleted() {
        return onboardingCompleted;
    }

    public void setOnboardingCompleted(boolean onboardingCompleted) {
        this.onboardingCompleted = onboardingCompleted;
    }

    public String getPictureUrl() {
        return pictureUrl;
    }

    public void setPictureUrl(String pictureUrl) {
        this.pictureUrl = pictureUrl;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public BigDecimal getMonthlyIncome() {
        return monthlyIncome;
    }

    public void setMonthlyIncome(BigDecimal monthlyIncome) {
        this.monthlyIncome = monthlyIncome;
    }

    public BigDecimal getMonthlySavingsGoal() {
        return monthlySavingsGoal;
    }

    public void setMonthlySavingsGoal(BigDecimal monthlySavingsGoal) {
        this.monthlySavingsGoal = monthlySavingsGoal;
    }

    public BigDecimal getEstimatedMonthlyExpense() {
        return estimatedMonthlyExpense;
    }

    public void setEstimatedMonthlyExpense(BigDecimal estimatedMonthlyExpense) {
        this.estimatedMonthlyExpense = estimatedMonthlyExpense;
    }

    public Category getPriorityCategory() {
        return priorityCategory;
    }

    public void setPriorityCategory(Category priorityCategory) {
        this.priorityCategory = priorityCategory;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}