package com.tindahanko.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
public class SemaphoreService {

    private static final Logger logger = LoggerFactory.getLogger(SemaphoreService.class);
    private static final String SEMAPHORE_API_URL = "https://api.semaphore.co/api/v4/messages";

    @Value("${semaphore.api.key:${SEMAPHORE_API_KEY:}}")
    private String apiKey;

    @Value("${semaphore.sender.name:${SEMAPHORE_SENDER_NAME:}}")
    private String senderName;

    private final RestTemplate restTemplate;

    public SemaphoreService() {
        this.restTemplate = new RestTemplate();
    }

    public SemaphoreService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * Checks if a live Semaphore API key is configured.
     */
    public boolean isLiveConfigured() {
        return apiKey != null 
                && !apiKey.trim().isEmpty() 
                && !apiKey.trim().equalsIgnoreCase("your_semaphore_api_key_here")
                && !apiKey.trim().equalsIgnoreCase("your_semaphore_api_key");
    }

    /**
     * Sends password reset verification code via Semaphore SMS API.
     * If live API key is missing, logs cleanly to the server console.
     *
     * @param mobileNumber Philippine mobile number (e.g., 09171234567 or +639171234567)
     * @param recipientName Full name of the recipient
     * @param verificationCode The generated token (e.g., TK-XXXXXXXX)
     * @param expiryMinutes Validity window in minutes
     * @return true if sent via live Semaphore API, false if simulated in server console
     */
    public boolean sendVerificationSms(String mobileNumber, String recipientName, String verificationCode, int expiryMinutes) {
        // Clean mobile number format for Semaphore (e.g. 09171234567)
        String cleanNumber = mobileNumber.trim().replaceAll("[\\s\\-\\(\\)]", "");
        if (cleanNumber.startsWith("+63")) {
            cleanNumber = "0" + cleanNumber.substring(3);
        }

        String smsText = String.format(
                "TindahanKo Security: Hello %s, your password reset verification code is: %s. Valid for %d minutes. Never share this code.",
                recipientName,
                verificationCode,
                expiryMinutes
        );

        if (isLiveConfigured()) {
            try {
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

                MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
                formData.add("apikey", apiKey.trim());
                formData.add("number", cleanNumber);
                formData.add("message", smsText);
                // Only add custom sendername if explicitly set and not the default
                if (senderName != null && !senderName.trim().isEmpty() && !senderName.trim().equalsIgnoreCase("SEMAPHORE")) {
                    formData.add("sendername", senderName.trim());
                }

                HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(formData, headers);
                ResponseEntity<String> response = restTemplate.postForEntity(SEMAPHORE_API_URL, requestEntity, String.class);

                logger.info("[SEMAPHORE API] SMS successfully dispatched to {}! Response status: {}, Body: {}", 
                        cleanNumber, response.getStatusCode(), response.getBody());

                System.out.println("\n================================================================================");
                System.out.println("[SEMAPHORE API - LIVE SMS SENT TO CARRIER]");
                System.out.println("  Recipient Phone  : " + cleanNumber);
                System.out.println("  Recipient Name   : " + recipientName);
                System.out.println("  Verification Code: " + verificationCode);
                System.out.println("  Status           : Sent via api.semaphore.co");
                System.out.println("================================================================================\n");

                return true;
            } catch (Exception e) {
                logger.error("[SEMAPHORE API ERROR] Failed to send SMS to {}: {}", cleanNumber, e.getMessage());
                // Fall back to server console logging if Semaphore network or credit fails
                logSimulatedSms(cleanNumber, recipientName, verificationCode, expiryMinutes, "SEMAPHORE API CALL FAILED (" + e.getMessage() + ")");
                return false;
            }
        } else {
            // Live key not yet configured in .env -> log simulated SMS
            logSimulatedSms(cleanNumber, recipientName, verificationCode, expiryMinutes, "SEMAPHORE_API_KEY not configured in .env");
            return false;
        }
    }

    private void logSimulatedSms(String number, String name, String code, int expiryMinutes, String reason) {
        System.out.println("\n================================================================================");
        System.out.println("[TINDANDANKO SMS DISPATCH - SEMAPHORE GATEWAY READY]");
        System.out.println("  Recipient Phone  : " + number);
        System.out.println("  Recipient Name   : " + name);
        System.out.println("  Verification Code: " + code);
        System.out.println("  Validity Period  : " + expiryMinutes + " minutes (Single-use)");
        System.out.println("  Dispatch Mode    : Local Server Console (" + reason + ")");
        System.out.println("  Live SMS Tip     : Add your SEMAPHORE_API_KEY in backend/.env to receive real SMS on your phone.");
        System.out.println("================================================================================\n");
    }
}
