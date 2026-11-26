package com.tindahanko.dto;

public class UserDTO {
    private Long userId;
    private String username;
    private String fullName;
    private String mobileNumber;
    private String email;
    private String role;

    public UserDTO() {}

    public UserDTO(Long userId, String username, String fullName, String mobileNumber, String role) {
        this(userId, username, fullName, mobileNumber, null, role);
    }

    public UserDTO(Long userId, String username, String fullName, String mobileNumber, String email, String role) {
        this.userId = userId;
        this.username = username;
        this.fullName = fullName;
        this.mobileNumber = mobileNumber;
        this.email = email;
        this.role = role;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
