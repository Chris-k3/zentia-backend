package com.zentia.app.identity.dto;

public class AuthResponseDTO {

    private String token;
    private Long userId;
    private String email;
    private String name;
    private boolean onboardingCompleted;
    private boolean isNewUser;

    public AuthResponseDTO() {
    }

    public AuthResponseDTO(String token, Long userId, String email, String name, boolean onboardingCompleted, boolean isNewUser) {
        this.token = token;
        this.userId = userId;
        this.email = email;
        this.name = name;
        this.onboardingCompleted = onboardingCompleted;
        this.isNewUser = isNewUser;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public boolean isNewUser() {
        return isNewUser;
    }

    public void setNewUser(boolean newUser) {
        isNewUser = newUser;
    }
}

