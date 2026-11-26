package com.tindahanko.dto;

public class ForgotPasswordResponse {
    private String message;
    private String maskedMobile;
    private int expiresInMinutes;
    private boolean success;
    private boolean liveSmsSent;

    public ForgotPasswordResponse() {}

    public ForgotPasswordResponse(String message, String maskedMobile, int expiresInMinutes, boolean success, boolean liveSmsSent) {
        this.message = message;
        this.maskedMobile = maskedMobile;
        this.expiresInMinutes = expiresInMinutes;
        this.success = success;
        this.liveSmsSent = liveSmsSent;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMaskedMobile() {
        return maskedMobile;
    }

    public void setMaskedMobile(String maskedMobile) {
        this.maskedMobile = maskedMobile;
    }

    public int getExpiresInMinutes() {
        return expiresInMinutes;
    }

    public void setExpiresInMinutes(int expiresInMinutes) {
        this.expiresInMinutes = expiresInMinutes;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public boolean isLiveSmsSent() {
        return liveSmsSent;
    }

    public void setLiveSmsSent(boolean liveSmsSent) {
        this.liveSmsSent = liveSmsSent;
    }
}
