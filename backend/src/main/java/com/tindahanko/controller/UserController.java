package com.tindahanko.controller;

import com.tindahanko.dto.ChangePasswordRequest;
import com.tindahanko.dto.MessageResponse;
import com.tindahanko.dto.ProfileResponse;
import com.tindahanko.dto.ProfileUpdateRequest;
import com.tindahanko.security.UserPrincipal;
import com.tindahanko.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/profile")
    public ResponseEntity<ProfileResponse> getProfile(@AuthenticationPrincipal UserPrincipal principal) {
        if (principal == null) {
            return ResponseEntity.status(401).build();
        }
        ProfileResponse response = userService.getProfile(principal.getUserId());
        return ResponseEntity.ok(response);
    }

    @PutMapping("/profile")
    public ResponseEntity<ProfileResponse> updateProfile(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody ProfileUpdateRequest request) {
        if (principal == null) {
            return ResponseEntity.status(401).build();
        }
        ProfileResponse response = userService.updateProfile(principal.getUserId(), request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/change-password")
    public ResponseEntity<MessageResponse> changePassword(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody ChangePasswordRequest request) {
        if (principal == null) {
            return ResponseEntity.status(401).build();
        }
        userService.changePassword(principal.getUserId(), request);
        return ResponseEntity.ok(MessageResponse.ok("Password has been changed successfully."));
    }
}
