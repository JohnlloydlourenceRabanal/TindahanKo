package com.tindahanko.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class ProfileUpdateRequest {

    @NotBlank(message = "Full name is required")
    @Size(min = 2, max = 100, message = "Full name must be between 2 and 100 characters")
    private String fullName;

    @NotBlank(message = "Mobile number is required")
    @Pattern(regexp = "^(09|\\+639)\\d{9}$", message = "Mobile number must be a valid 11-digit Philippine number (e.g., 09171234567)")
    private String mobileNumber;

    @jakarta.validation.constraints.Email(message = "Please provide a valid email address")
    private String email;

    public ProfileUpdateRequest() {}

    public ProfileUpdateRequest(String fullName, String mobileNumber) {
        this.fullName = fullName;
        this.mobileNumber = mobileNumber;
    }

    public ProfileUpdateRequest(String fullName, String mobileNumber, String email) {
        this.fullName = fullName;
        this.mobileNumber = mobileNumber;
        this.email = email;
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
}
