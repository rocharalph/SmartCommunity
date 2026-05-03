package com.example.smartcommunity;

public class UserModel {

    private String fullName;
    private String email;
    private String studentId;
    private String contactNumber;
    private String address;
    private String role;
    private String status;
    private String profileImage;

    public UserModel() {
    }

    public UserModel(String fullName, String email, String studentId, String contactNumber,
                     String address, String role, String status, String profileImage) {
        this.fullName = fullName;
        this.email = email;
        this.studentId = studentId;
        this.contactNumber = contactNumber;
        this.address = address;
        this.role = role;
        this.status = status;
        this.profileImage = profileImage;
    }

    public String getFullName() {
        return fullName;
    }

    public String getEmail() {
        return email;
    }

    public String getStudentId() {
        return studentId;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public String getAddress() {
        return address;
    }

    public String getRole() {
        return role;
    }

    public String getStatus() {
        return status;
    }

    public String getProfileImage() {
        return profileImage;
    }
}