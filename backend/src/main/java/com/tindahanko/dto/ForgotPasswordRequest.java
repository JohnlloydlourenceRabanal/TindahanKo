package com.tindahanko.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class ForgotPasswordRequest {

    @NotBlank(message = "Username is required for identity verification")
    private String username;

    @NotBlank(message = "Registered Philippine mobile number is required")
    @Pattern(regexp = "^(09|\\+639)\\d{9}$", message = "Mobile number must be a valid Philippine number (e.g., 09171234567 or +639171234567)")
    private String mobileNumber;

    @NotBlank(message = "Registered full name is required for identity verification")
    private String fullName;

    public ForgotPasswordRequest() {}

    public ForgotPasswordRequest(String username, String mobileNumber, String fullName) {
        this.username = username;
        this.mobileNumber = mobileNumber;
        this.fullName = fullName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
}
