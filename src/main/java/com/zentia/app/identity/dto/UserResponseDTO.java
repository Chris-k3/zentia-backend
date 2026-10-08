package com.zentia.app.identity.dto;

public class UserResponseDTO {
    //DATOS SEGUROS PARA RESPONDER AL CLIENTE
    private long id;
    private String name; 
    private String email;
    private String PictureUrl;
    private boolean onboardingCompleted;

    //Constructor vacio necesario para algunas librerias
    public UserResponseDTO() {
    }
    //Constructor para llenar los datos facilmente desde el servicio
    public UserResponseDTO(long id, String name, String email, String pictureUrl, boolean onboardingCompleted) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.PictureUrl = pictureUrl;
        this.onboardingCompleted = onboardingCompleted;
        
    }
            //getters and setters
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
        return PictureUrl;
    }
    public void setPictureUrl(String pictureUrl) {
        PictureUrl = pictureUrl;
    }
    public boolean isOnboardingCompleted() {
        return onboardingCompleted;
    }
    public void setOnboardingCompleted(boolean onboardingCompleted) {
        this.onboardingCompleted = onboardingCompleted;
    }

}
