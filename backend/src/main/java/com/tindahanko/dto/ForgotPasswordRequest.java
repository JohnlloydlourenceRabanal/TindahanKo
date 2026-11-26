package com.tindahanko.dto;

import jakarta.validation.constraints.NotBlank;

public class ForgotPasswordRequest {

    @NotBlank(message = "Username or Philippine mobile number is required")
    private String usernameOrMobile;

    public ForgotPasswordRequest() {}

    public ForgotPasswordRequest(String usernameOrMobile) {
        this.usernameOrMobile = usernameOrMobile;
    }

    public String getUsernameOrMobile() {
        return usernameOrMobile;
    }

    public void setUsernameOrMobile(String usernameOrMobile) {
        this.usernameOrMobile = usernameOrMobile;
    }
}
