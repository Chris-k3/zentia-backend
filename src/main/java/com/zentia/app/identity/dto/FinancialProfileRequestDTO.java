package com.zentia.app.identity.dto;

import java.math.BigDecimal;

public class FinancialProfileRequestDTO {

    private BigDecimal monthlyIncome;
    private BigDecimal monthlySavingsGoal;
    private BigDecimal estimatedMonthlyExpense;
    private Long priorityCategoryId;

    public FinancialProfileRequestDTO() {
    }

    public FinancialProfileRequestDTO(
            BigDecimal monthlyIncome,
            BigDecimal monthlySavingsGoal,
            BigDecimal estimatedMonthlyExpense,
            Long priorityCategoryId) {
        this.monthlyIncome = monthlyIncome;
        this.monthlySavingsGoal = monthlySavingsGoal;
        this.estimatedMonthlyExpense = estimatedMonthlyExpense;
        this.priorityCategoryId = priorityCategoryId;
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
}

