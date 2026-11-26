package com.tindahanko.dto;

public class ForgotPasswordResponse {
    private String message;
    private String maskedMobile;
    private String maskedEmail;
    private int expiresInMinutes;
    private boolean success;
    private boolean liveSmsSent;
    private boolean liveEmailSent;

    public ForgotPasswordResponse() {}

    public ForgotPasswordResponse(String message, String maskedMobile, String maskedEmail, int expiresInMinutes, boolean success, boolean liveSmsSent, boolean liveEmailSent) {
        this.message = message;
        this.maskedMobile = maskedMobile;
        this.maskedEmail = maskedEmail;
        this.expiresInMinutes = expiresInMinutes;
        this.success = success;
        this.liveSmsSent = liveSmsSent;
        this.liveEmailSent = liveEmailSent;
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

    public String getMaskedEmail() {
        return maskedEmail;
    }

    public void setMaskedEmail(String maskedEmail) {
        this.maskedEmail = maskedEmail;
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

    public boolean isLiveEmailSent() {
        return liveEmailSent;
    }

    public void setLiveEmailSent(boolean liveEmailSent) {
        this.liveEmailSent = liveEmailSent;
    }
}
