package com.fastfood.userservice.dto;

import com.fastfood.userservice.enums.UserStatus;

import java.time.LocalDateTime;

public class UserProfileResponse {

    private Long id;
    private String username;
    private String email;
    private String role;
    private UserStatus status;
    private Boolean emailVerified;
    private LocalDateTime registeredDate;
    private LocalDateTime lastLoginDate;
    private LocalDateTime passwordUpdatedDate;

    public UserProfileResponse() {
    }

    public UserProfileResponse(Long id, String username, String email, String role, UserStatus status, Boolean emailVerified, LocalDateTime registeredDate, LocalDateTime lastLoginDate, LocalDateTime passwordUpdatedDate) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.role = role;
        this.status = status;
        this.emailVerified = emailVerified;
        this.registeredDate = registeredDate;
        this.lastLoginDate = lastLoginDate;
        this.passwordUpdatedDate = passwordUpdatedDate;
    }
    public Long getId() {
        return id;
    }
    public String getUsername() {
        return username;
    }
    public String getEmail() {
        return email;
    }
    public String getRole() {
        return role;
    }
    public UserStatus getStatus() {
        return status;
    }
    public Boolean getEmailVerified() {
        return emailVerified;
    }
    public LocalDateTime getRegisteredDate() {
        return registeredDate;
    }
    public LocalDateTime getLastLoginDate() {
        return lastLoginDate;
    }
    public LocalDateTime getPasswordUpdatedDate() {
        return passwordUpdatedDate;
    }
}