package com.example.demo.dto;

import java.util.UUID;

public class LoginResponseDTO {
    private Integer userId;
    private String username;
    private String sessionToken;
    private String message;

    // Constructor
    public LoginResponseDTO(Integer userId, String username, String sessionToken, String message) {
        this.userId = userId;
        this.username = username;
        this.sessionToken = sessionToken;
        this.message = message;
    }


    // Getters and setters
    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getSessionToken() {
        return sessionToken;
    }

    public void setSessionToken(String sessionToken) {
        this.sessionToken = sessionToken;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}