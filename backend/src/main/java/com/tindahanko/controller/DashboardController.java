package com.tindahanko.controller;

import com.tindahanko.dto.UserDTO;
import com.tindahanko.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class DashboardController {

    private final AuthService authService;

    public DashboardController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * Store Owner (Admin) Dashboard endpoint
     * Strictly restricted to users with ROLE_STORE_OWNER
     */
    @GetMapping("/owner/dashboard")
    @PreAuthorize("hasRole('STORE_OWNER')")
    public ResponseEntity<Map<String, Object>> getOwnerDashboard(Authentication authentication) {
        UserDTO user = authService.getCurrentUser(authentication.getName());

        Map<String, Object> data = new HashMap<>();
        data.put("role", "STORE_OWNER");
        data.put("title", "Sari-Sari Store Owner Command Center");
        data.put("ownerName", user.getFullName());
        data.put("username", user.getUsername());
        data.put("mobileNumber", user.getMobileNumber());
        data.put("serverTime", LocalDateTime.now());
        data.put("stats", Map.of(
                "totalProducts", 0,
                "lowStockCount", 0,
                "totalReceivables", "PHP 0.00",
                "activeDebtorsCount", 0
        ));
        data.put("message", "Welcome back, Store Owner! Increment 1 Authentication and RBAC operational.");

        return ResponseEntity.ok(data);
    }

    /**
     * Store Customer (Borrower) Dashboard endpoint
     * Strictly restricted to users with ROLE_CUSTOMER
     */
    @GetMapping("/customer/dashboard")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<Map<String, Object>> getCustomerDashboard(Authentication authentication) {
        UserDTO user = authService.getCurrentUser(authentication.getName());

        Map<String, Object> data = new HashMap<>();
        data.put("role", "CUSTOMER");
        data.put("title", "Customer Utang & Balance Portal");
        data.put("customerName", user.getFullName());
        data.put("username", user.getUsername());
        data.put("mobileNumber", user.getMobileNumber());
        data.put("serverTime", LocalDateTime.now());
        data.put("accountOverview", Map.of(
                "currentUtangBalance", "PHP 0.00",
                "creditLimit", "PHP 2,000.00",
                "dueDate", "No active due date",
                "paymentStatus", "Good Standing"
        ));
        data.put("message", "Welcome to TindahanKo Customer Portal! Increment 1 Authentication and RBAC operational.");

        return ResponseEntity.ok(data);
    }
}
