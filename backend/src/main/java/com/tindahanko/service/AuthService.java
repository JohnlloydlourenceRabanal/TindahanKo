package com.tindahanko.service;

import com.tindahanko.dto.*;
import com.tindahanko.entity.PasswordResetToken;
import com.tindahanko.entity.Role;
import com.tindahanko.entity.User;
import com.tindahanko.repository.PasswordResetTokenRepository;
import com.tindahanko.repository.UserRepository;
import com.tindahanko.security.JwtUtils;
import com.tindahanko.security.UserPrincipal;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;

    public AuthService(UserRepository userRepository,
                       PasswordResetTokenRepository passwordResetTokenRepository,
                       PasswordEncoder passwordEncoder,
                       AuthenticationManager authenticationManager,
                       JwtUtils jwtUtils) {
        this.userRepository = userRepository;
        this.passwordResetTokenRepository = passwordResetTokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        // Validate password confirmation match
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new IllegalArgumentException("Passwords do not match");
        }

        // Validate username uniqueness
        if (userRepository.existsByUsername(request.getUsername().trim())) {
            throw new IllegalArgumentException("Username '" + request.getUsername().trim() + "' is already registered");
        }

        // Validate Philippine mobile number uniqueness
        String cleanMobile = request.getMobileNumber().trim();
        if (userRepository.existsByMobileNumber(cleanMobile)) {
            throw new IllegalArgumentException("Mobile number '" + cleanMobile + "' is already registered to an account");
        }

        // Determine Role (Strictly either STORE_OWNER or CUSTOMER)
        Role assignedRole = Role.CUSTOMER;
        if (request.getRole() != null && !request.getRole().trim().isEmpty()) {
            String roleUpper = request.getRole().trim().toUpperCase();
            if ("STORE_OWNER".equals(roleUpper) || "ADMIN".equals(roleUpper)) {
                assignedRole = Role.STORE_OWNER;
            } else if ("CUSTOMER".equals(roleUpper)) {
                assignedRole = Role.CUSTOMER;
            } else {
                throw new IllegalArgumentException("Invalid role specified. Allowed roles: STORE_OWNER, CUSTOMER");
            }
        }

        // Securely hash password with BCrypt
        String passwordHash = passwordEncoder.encode(request.getPassword());

        User newUser = new User(
                request.getUsername().trim(),
                passwordHash,
                request.getFullName().trim(),
                cleanMobile,
                assignedRole
        );

        User savedUser = userRepository.save(newUser);

        // Generate JWT token
        String token = jwtUtils.generateToken(
                savedUser.getUsername(),
                savedUser.getRole().name(),
                savedUser.getUserId()
        );

        return new AuthResponse(
                token,
                savedUser.getUserId(),
                savedUser.getUsername(),
                savedUser.getFullName(),
                savedUser.getMobileNumber(),
                savedUser.getRole().name(),
                "Registration successful"
        );
    }

    public AuthResponse login(LoginRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername().trim(), request.getPassword())
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();

            String token = jwtUtils.generateToken(
                    principal.getUsername(),
                    principal.getRoleName(),
                    principal.getUserId()
            );

            return new AuthResponse(
                    token,
                    principal.getUserId(),
                    principal.getUsername(),
                    principal.getFullName(),
                    principal.getMobileNumber(),
                    principal.getRoleName(),
                    "Login successful"
            );
        } catch (BadCredentialsException e) {
            // Strict security rule: Never reveal whether username or password was incorrect
            throw new BadCredentialsException("Invalid username or password");
        }
    }

    public UserDTO getCurrentUser(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + username));
        return new UserDTO(
                user.getUserId(),
                user.getUsername(),
                user.getFullName(),
                user.getMobileNumber(),
                user.getRole().name()
        );
    }

    @Transactional
    public ForgotPasswordResponse requestPasswordReset(ForgotPasswordRequest request) {
        String username = request.getUsername().trim();
        String mobileNumber = request.getMobileNumber().trim().replaceAll("[\\s\\-\\(\\)]", "");
        String fullName = request.getFullName().trim();

        // Strict multi-factor identity verification: username, mobile number, and full name must all match
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Identity verification failed: The provided username, mobile number, and full name do not match our records."));

        // Verify registered mobile number
        String userMobile = user.getMobileNumber().replaceAll("[\\s\\-\\(\\)]", "");
        if (!userMobile.equals(mobileNumber)) {
            throw new IllegalArgumentException("Identity verification failed: The provided username, mobile number, and full name do not match our records.");
        }

        // Verify registered full name (case-insensitive)
        if (!user.getFullName().trim().equalsIgnoreCase(fullName)) {
            throw new IllegalArgumentException("Identity verification failed: The provided username, mobile number, and full name do not match our records.");
        }

        // Invalidate any existing unused reset tokens for this user
        passwordResetTokenRepository.deleteByUser(user);

        // Generate a secure, single-use 8-character verification token
        String token = "TK-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        int expiryMinutes = 15;
        LocalDateTime expiry = LocalDateTime.now().plusMinutes(expiryMinutes);

        PasswordResetToken resetToken = new PasswordResetToken(user, token, expiry);
        passwordResetTokenRepository.save(resetToken);

        // Mask phone number for user privacy and security (e.g., 0917****888)
        String rawPhone = user.getMobileNumber();
        String maskedMobile = rawPhone.length() >= 7
                ? rawPhone.substring(0, 4) + "****" + rawPhone.substring(rawPhone.length() - 3)
                : rawPhone;

        // Dispatch simulated SMS to the backend server console log
        // (Out-of-band delivery prevents unauthorized web users from seeing or intercepting the code)
        System.out.println("\n================================================================================");
        System.out.println("[TINDANDANKO SMS GATEWAY - SECURE OUTGOING SMS DISPATCH]");
        System.out.println("  Recipient Name   : " + user.getFullName());
        System.out.println("  Username         : " + user.getUsername());
        System.out.println("  Recipient Mobile : " + user.getMobileNumber());
        System.out.println("  Verification Code: " + token);
        System.out.println("  Validity Period  : " + expiryMinutes + " minutes (Single-use only)");
        System.out.println("  Security Status  : Dispatched to user's mobile device via SMS.");
        System.out.println("  Note             : NEVER share this verification code with anyone.");
        System.out.println("================================================================================\n");

        // The HTTP response intentionally NEVER leaks the token to the client browser
        return new ForgotPasswordResponse(
                "Identity verified successfully. A single-use verification code has been dispatched via SMS to your registered mobile number (" + maskedMobile + ").",
                maskedMobile,
                expiryMinutes,
                true
        );
    }

    @Transactional
    public void resetPassword(ResetPasswordRequest request) {
        String rawToken = request.getToken().trim();

        PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(rawToken)
                .orElseThrow(() -> new IllegalArgumentException("Invalid or unrecognized password reset token."));

        if (resetToken.isUsed()) {
            throw new IllegalArgumentException("This password reset token has already been used.");
        }

        if (resetToken.isExpired()) {
            throw new IllegalArgumentException("This password reset token has expired. Please request a new verification code.");
        }

        // Validate password confirmation match
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new IllegalArgumentException("New password and confirmation do not match.");
        }

        // Validate minimum length
        if (request.getNewPassword().length() < 8) {
            throw new IllegalArgumentException("New password must be at least 8 characters long.");
        }

        User user = resetToken.getUser();

        // Ensure new password differs from current password
        if (passwordEncoder.matches(request.getNewPassword(), user.getPasswordHash())) {
            throw new IllegalArgumentException("New password cannot be the same as your current password.");
        }

        // Hash new password with BCrypt (strength 10)
        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        // Mark token as consumed (single-use enforcement)
        resetToken.setUsed(true);
        passwordResetTokenRepository.save(resetToken);
    }
}
