package com.example.smartcommunity;

public class UserItemModel {
    private String userId;
    private String fullName;
    private String email;
    private String role;
    private String status;

    public UserItemModel() {
    }

    public UserItemModel(String userId, String fullName, String email, String role, String status) {
        this.userId = userId;
        this.fullName = fullName;
        this.email = email;
        this.role = role;
        this.status = status;
    }

    public String getUserId() {
        return userId;
    }

    public String getFullName() {
        return fullName;
    }

    public String getEmail() {
        return email;
    }

    public String getRole() {
        return role;
    }

    public String getStatus() {
        return status;
    }
}