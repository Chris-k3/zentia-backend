package com.zentia.app.finance.dto;
//Esta clase es la que se devuelve al front para mostrar los datos de la transaccion
public class TransactionResponseDTO {
    private long id; //id de la transaccion generado en la base de datos
    private double amount;
    private String date;
    private String type;
    private String description;
    private String categoryName; //en lugar de devolver el objeto completo de categoria, devolvemos solo el nombre para no exponer datos innecesarios al front

    public TransactionResponseDTO() {
    }

    public TransactionResponseDTO(long id, String description, double amount, String date, String type, String categoryName) {
        this.id = id;
        this.description = description;
        this.amount = amount;
        this.date = date;
        this.type = type;
        this.categoryName = categoryName;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }
}
