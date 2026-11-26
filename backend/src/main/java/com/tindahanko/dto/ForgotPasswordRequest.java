package com.tindahanko.dto;

import jakarta.validation.constraints.NotBlank;

public class ForgotPasswordRequest {

    @NotBlank(message = "Username is required for identity verification")
    private String username;

    @NotBlank(message = "Registered full name is required for identity verification")
    private String fullName;

    // Optional: At least one of mobileNumber or email must be provided
    private String mobileNumber;

    private String email;

    // Optional: "email" or "mobile"
    private String method;

    public ForgotPasswordRequest() {}

    public ForgotPasswordRequest(String username, String mobileNumber, String fullName) {
        this.username = username;
        this.mobileNumber = mobileNumber;
        this.fullName = fullName;
    }

    public ForgotPasswordRequest(String username, String mobileNumber, String email, String fullName, String method) {
        this.username = username;
        this.mobileNumber = mobileNumber;
        this.email = email;
        this.fullName = fullName;
        this.method = method;
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

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }
}
