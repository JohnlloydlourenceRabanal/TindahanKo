package com.tindahanko.service;

import com.tindahanko.dto.ChangePasswordRequest;
import com.tindahanko.dto.ProfileResponse;
import com.tindahanko.dto.ProfileUpdateRequest;
import com.tindahanko.entity.User;
import com.tindahanko.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public ProfileResponse getProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User profile not found."));

        return new ProfileResponse(
                user.getUserId(),
                user.getUsername(),
                user.getFullName(),
                user.getMobileNumber(),
                user.getRole().name(),
                user.getCreatedAt()
        );
    }

    @Transactional
    public ProfileResponse updateProfile(Long userId, ProfileUpdateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User profile not found."));

        String cleanName = request.getFullName().trim();
        String cleanMobile = request.getMobileNumber().trim();

        if (cleanName.length() < 2 || cleanName.length() > 100) {
            throw new IllegalArgumentException("Full name must be between 2 and 100 characters.");
        }

        if (!cleanMobile.matches("^(09|\\+639)\\d{9}$")) {
            throw new IllegalArgumentException("Mobile number must be a valid 11-digit Philippine number (e.g., 09171234567).");
        }

        // Prevent duplicate mobile numbers across different users
        if (userRepository.existsByMobileNumberAndUserIdNot(cleanMobile, userId)) {
            throw new IllegalArgumentException("Mobile number '" + cleanMobile + "' is already registered to another user.");
        }

        user.setFullName(cleanName);
        user.setMobileNumber(cleanMobile);

        User saved = userRepository.save(user);

        return new ProfileResponse(
                saved.getUserId(),
                saved.getUsername(),
                saved.getFullName(),
                saved.getMobileNumber(),
                saved.getRole().name(),
                saved.getCreatedAt()
        );
    }

    @Transactional
    public void changePassword(Long userId, ChangePasswordRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User profile not found."));

        // 1. Verify current password
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPasswordHash())) {
            throw new IllegalArgumentException("Current password is incorrect.");
        }

        // 2. Verify new password match
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new IllegalArgumentException("New password and confirmation do not match.");
        }

        // 3. Verify length
        if (request.getNewPassword().length() < 8) {
            throw new IllegalArgumentException("New password must be at least 8 characters long.");
        }

        // 4. Verify new password differs from current password
        if (passwordEncoder.matches(request.getNewPassword(), user.getPasswordHash())) {
            throw new IllegalArgumentException("New password cannot be the same as your current password.");
        }

        // 5. Hash new password with BCrypt and persist
        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }
}
