package com.tindahanko.service;

import com.tindahanko.dto.AuthResponse;
import com.tindahanko.dto.LoginRequest;
import com.tindahanko.dto.RegisterRequest;
import com.tindahanko.dto.UserDTO;
import com.tindahanko.entity.Role;
import com.tindahanko.entity.User;
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

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       AuthenticationManager authenticationManager,
                       JwtUtils jwtUtils) {
        this.userRepository = userRepository;
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
}
