package com.tindahanko.service;

import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Service
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);
    private static final String RESEND_API_URL = "https://api.resend.com/emails";

    @Autowired(required = false)
    private JavaMailSender mailSender;

    @Value("${spring.mail.username:${SPRING_MAIL_USERNAME:}}")
    private String mailFrom;

    @Value("${resend.api.key:${RESEND_API_KEY:}}")
    private String resendApiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    /**
     * Checks if a live email sending service (Gmail SMTP or Resend API) is configured.
     */
    public boolean isLiveConfigured() {
        boolean hasGmail = mailSender != null && mailFrom != null && !mailFrom.trim().isEmpty();
        boolean hasResend = resendApiKey != null && !resendApiKey.trim().isEmpty() && !resendApiKey.equalsIgnoreCase("your_resend_api_key");
        return hasGmail || hasResend;
    }

    /**
     * Sends password reset verification email.
     * Tries Gmail SMTP first, then Resend API, and falls back to server console logging.
     *
     * @param toEmail Recipient email address
     * @param recipientName Full name of the user
     * @param verificationCode The generated token (e.g. TK-XXXXXXXX)
     * @param expiryMinutes Validity window in minutes
     * @return true if sent via a live provider, false if simulated
     */
    public boolean sendVerificationEmail(String toEmail, String recipientName, String verificationCode, int expiryMinutes) {
        String subject = "TindahanKo - Password Reset Verification Code (" + verificationCode + ")";
        String htmlBody = buildHtmlTemplate(recipientName, verificationCode, expiryMinutes);

        // 1. Try Gmail SMTP if configured
        if (mailSender != null && mailFrom != null && !mailFrom.trim().isEmpty()) {
            try {
                MimeMessage mimeMessage = mailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");
                helper.setFrom(mailFrom, "TindahanKo Security");
                helper.setTo(toEmail.trim());
                helper.setSubject(subject);
                helper.setText(htmlBody, true);

                mailSender.send(mimeMessage);
                logger.info("[GMAIL SMTP] Verification email successfully delivered to {} via Gmail!", toEmail);
                logSuccessBanner("GMAIL SMTP (smtp.gmail.com)", toEmail, recipientName, verificationCode);
                return true;
            } catch (Exception e) {
                logger.warn("[GMAIL SMTP ERROR] Could not send via Gmail SMTP: {}", e.getMessage());
            }
        }

        // 2. Try Resend API if configured
        if (resendApiKey != null && !resendApiKey.trim().isEmpty() && !resendApiKey.equalsIgnoreCase("your_resend_api_key")) {
            try {
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                headers.setBearerAuth(resendApiKey.trim());

                Map<String, Object> payload = new HashMap<>();
                payload.put("from", "TindahanKo <onboarding@resend.dev>");
                payload.put("to", Collections.singletonList(toEmail.trim()));
                payload.put("subject", subject);
                payload.put("html", htmlBody);

                HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(payload, headers);
                ResponseEntity<String> response = restTemplate.postForEntity(RESEND_API_URL, requestEntity, String.class);

                logger.info("[RESEND API] Verification email successfully delivered to {}! Response: {}", toEmail, response.getBody());
                logSuccessBanner("RESEND API (api.resend.com)", toEmail, recipientName, verificationCode);
                return true;
            } catch (Exception e) {
                logger.warn("[RESEND API ERROR] Could not send via Resend API: {}", e.getMessage());
            }
        }

        // 3. Fallback: Log to server console
        logSimulatedEmail(toEmail, recipientName, verificationCode, expiryMinutes);
        return false;
    }

    private void logSuccessBanner(String provider, String toEmail, String name, String code) {
        System.out.println("\n================================================================================");
        System.out.println("[TINDANDANKO EMAIL GATEWAY - LIVE EMAIL DISPATCHED]");
        System.out.println("  Provider         : " + provider);
        System.out.println("  Recipient Email  : " + toEmail);
        System.out.println("  Recipient Name   : " + name);
        System.out.println("  Verification Code: " + code);
        System.out.println("  Status           : Delivered to recipient's Gmail inbox.");
        System.out.println("================================================================================\n");
    }

    private void logSimulatedEmail(String toEmail, String name, String code, int expiryMinutes) {
        System.out.println("\n================================================================================");
        System.out.println("[TINDANDANKO EMAIL DISPATCH - CONSOLE SIMULATOR]");
        System.out.println("  Recipient Email  : " + toEmail);
        System.out.println("  Recipient Name   : " + name);
        System.out.println("  Verification Code: " + code);
        System.out.println("  Validity Period  : " + expiryMinutes + " minutes (Single-use)");
        System.out.println("  Live Email Setup : Configure Gmail App Password or RESEND_API_KEY in backend/.env");
        System.out.println("================================================================================\n");
    }

    private String buildHtmlTemplate(String name, String code, int expiryMinutes) {
        return "<!DOCTYPE html>"
                + "<html>"
                + "<head><meta charset='UTF-8'></head>"
                + "<body style='font-family: Arial, sans-serif; background-color: #f1f5f9; padding: 30px; margin: 0;'>"
                + "  <div style='max-width: 540px; margin: 0 auto; background: #ffffff; border-radius: 12px; border: 1px solid #e2e8f0; overflow: hidden; box-shadow: 0 4px 6px -1px rgba(0,0,0,0.1);'>"
                + "    <div style='background: #163C63; padding: 24px; text-align: center; color: #ffffff;'>"
                + "      <h1 style='margin: 0; font-size: 22px; font-weight: bold;'>TindahanKo Security</h1>"
                + "      <p style='margin: 6px 0 0 0; font-size: 13px; opacity: 0.9;'>Password Recovery Verification</p>"
                + "    </div>"
                + "    <div style='padding: 28px 24px; color: #0f172a;'>"
                + "      <p style='margin: 0 0 16px 0; font-size: 15px;'>Hello <strong>" + name + "</strong>,</p>"
                + "      <p style='margin: 0 0 20px 0; font-size: 14px; line-height: 1.6; color: #334155;'>We received a request to reset your TindahanKo account password. Use the single-use verification code below to complete your reset:</p>"
                + "      <div style='background: #f8fafc; border: 2px dashed #205A88; border-radius: 8px; padding: 18px; text-align: center; margin: 20px 0;'>"
                + "        <span style='font-family: monospace; font-size: 28px; font-weight: bold; letter-spacing: 4px; color: #163C63;'>" + code + "</span>"
                + "      </div>"
                + "      <p style='margin: 0 0 8px 0; font-size: 13px; color: #64748b;'>⏰ This code is valid for <strong>" + expiryMinutes + " minutes</strong> and can only be used once.</p>"
                + "      <p style='margin: 0; font-size: 13px; color: #dc2626;'>🔒 If you did not request this password reset, please ignore this email or review your account security.</p>"
                + "    </div>"
                + "    <div style='background: #f8fafc; border-top: 1px solid #e2e8f0; padding: 14px 24px; text-align: center; font-size: 12px; color: #94a3b8;'>"
                + "      &copy; 2026 TindahanKo - Sari-Sari Store Management System. All rights reserved."
                + "    </div>"
                + "  </div>"
                + "</body>"
                + "</html>";
    }
}
