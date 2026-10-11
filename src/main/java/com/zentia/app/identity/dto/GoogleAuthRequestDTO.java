package com.zentia.app.identity.dto;

public class GoogleAuthRequestDTO {

    private String idToken;
    private String googleId;
    private String email;
    private String name;
    private String pictureUrl;

    public GoogleAuthRequestDTO() {
    }

    public GoogleAuthRequestDTO(String idToken, String googleId, String email, String name, String pictureUrl) {
        this.idToken = idToken;
        this.googleId = googleId;
        this.email = email;
        this.name = name;
        this.pictureUrl = pictureUrl;
    }

    public String getIdToken() {
        return idToken;
    }

    public void setIdToken(String idToken) {
        this.idToken = idToken;
    }

    public String getGoogleId() {
        return googleId;
    }

    public void setGoogleId(String googleId) {
        this.googleId = googleId;
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

    public String getPictureUrl() {
        return pictureUrl;
    }

    public void setPictureUrl(String pictureUrl) {
        this.pictureUrl = pictureUrl;
    }
}

