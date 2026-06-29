package com.fastfood.userservice.dto;

public class RegisterResponse {
    private Long userId;
    private String username;
    private String email;

    public RegisterResponse() {
    }
    public RegisterResponse(Long userId, String username, String email) {
        this.userId = userId;
        this.username = username;
        this.email = email;
    }
    public Long getUserId() {
        return userId;
    }
    public String getUsername() {
        return username;
    }
    public String getEmail() {
        return email;
    }
}