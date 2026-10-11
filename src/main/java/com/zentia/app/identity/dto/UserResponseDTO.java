package com.zentia.app.identity.dto;

public class UserResponseDTO {
    // DATOS SEGUROS PARA RESPONDER AL CLIENTE
    private long id;
    private String name; 
    private String email;
    private String pictureUrl;
    private boolean onboardingCompleted;
    private String phoneNumber;

    // Constructor vacío necesario para deserialización
    public UserResponseDTO() {
    }

    // Constructor completo con teléfono
    public UserResponseDTO(long id, String name, String email, String pictureUrl, boolean onboardingCompleted, String phoneNumber) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.pictureUrl = pictureUrl;
        this.onboardingCompleted = onboardingCompleted;
        this.phoneNumber = phoneNumber;
    }

    // Constructor anterior para compatibilidad
    public UserResponseDTO(long id, String name, String email, String pictureUrl, boolean onboardingCompleted) {
        this(id, name, email, pictureUrl, onboardingCompleted, null);
    }

    // Getters and Setters
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPictureUrl() {
        return pictureUrl;
    }

    public void setPictureUrl(String pictureUrl) {
        this.pictureUrl = pictureUrl;
    }

    public boolean isOnboardingCompleted() {
        return onboardingCompleted;
    }

    public void setOnboardingCompleted(boolean onboardingCompleted) {
        this.onboardingCompleted = onboardingCompleted;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
