package com.tindahanko.dto;

public class ForgotPasswordResponse {
    private String message;
    private String resetToken;
    private int expiresInMinutes;

    public ForgotPasswordResponse() {}

    public ForgotPasswordResponse(String message, String resetToken, int expiresInMinutes) {
        this.message = message;
        this.resetToken = resetToken;
        this.expiresInMinutes = expiresInMinutes;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getResetToken() {
        return resetToken;
    }

    public void setResetToken(String resetToken) {
        this.resetToken = resetToken;
    }

    public int getExpiresInMinutes() {
        return expiresInMinutes;
    }

    public void setExpiresInMinutes(int expiresInMinutes) {
        this.expiresInMinutes = expiresInMinutes;
    }
}
