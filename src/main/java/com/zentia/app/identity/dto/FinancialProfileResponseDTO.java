package com.zentia.app.identity.dto;

import java.math.BigDecimal;

public class FinancialProfileResponseDTO {

    private Long userId;
    private BigDecimal monthlyIncome;
    private BigDecimal monthlySavingsGoal;
    private BigDecimal estimatedMonthlyExpense;
    private Long priorityCategoryId;
    private String priorityCategoryName;
    private boolean onboardingCompleted;

    public FinancialProfileResponseDTO() {
    }

    public FinancialProfileResponseDTO(
            Long userId,
            BigDecimal monthlyIncome,
            BigDecimal monthlySavingsGoal,
            BigDecimal estimatedMonthlyExpense,
            Long priorityCategoryId,
            String priorityCategoryName,
            boolean onboardingCompleted) {
        this.userId = userId;
        this.monthlyIncome = monthlyIncome;
        this.monthlySavingsGoal = monthlySavingsGoal;
        this.estimatedMonthlyExpense = estimatedMonthlyExpense;
        this.priorityCategoryId = priorityCategoryId;
        this.priorityCategoryName = priorityCategoryName;
        this.onboardingCompleted = onboardingCompleted;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
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

    public Long getPriorityCategoryId() {
        return priorityCategoryId;
    }

    public void setPriorityCategoryId(Long priorityCategoryId) {
        this.priorityCategoryId = priorityCategoryId;
    }

    public String getPriorityCategoryName() {
        return priorityCategoryName;
    }

    public void setPriorityCategoryName(String priorityCategoryName) {
        this.priorityCategoryName = priorityCategoryName;
    }

    public boolean isOnboardingCompleted() {
        return onboardingCompleted;
    }

    public void setOnboardingCompleted(boolean onboardingCompleted) {
        this.onboardingCompleted = onboardingCompleted;
    }
}

