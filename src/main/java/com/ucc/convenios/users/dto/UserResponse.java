package com.ucc.convenios.users.dto;

import com.ucc.convenios.users.entity.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class UserResponse {

    private UUID id;
    private String fullName;
    private String email;
    private Boolean emailVerified;
    private Boolean active;
    private String authProvider;
    private List<String> roles;
    private LocalDateTime createdAt;

    public UserResponse() {
    }

    public static UserResponse fromEntity(User user, List<String> roles) {
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setFullName(user.getFullName());
        response.setEmail(user.getEmail());
        response.setEmailVerified(user.getEmailVerified());
        response.setActive(user.getActive());
        response.setAuthProvider(user.getAuthProvider().name());
        response.setRoles(roles);
        response.setCreatedAt(user.getCreatedAt());
        return response;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Boolean getEmailVerified() {
        return emailVerified;
    }

    public void setEmailVerified(Boolean emailVerified) {
        this.emailVerified = emailVerified;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public String getAuthProvider() {
        return authProvider;
    }

    public void setAuthProvider(String authProvider) {
        this.authProvider = authProvider;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}