package com.ridelink.accountservice.controller;

import com.ridelink.accountservice.dto.UpdateProfileRequest;
import com.ridelink.accountservice.dto.UpdateStatusRequest;
import com.ridelink.accountservice.dto.UserResponse;
import com.ridelink.accountservice.model.Role;
import com.ridelink.accountservice.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/accounts")
@Tag(name = "Account Management", description = "Endpoints for Profile Viewing, Updating, and Account Status Management")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/profile")
    @Operation(summary = "Get user profile", description = "Retrieves profile of current authenticated user or by userId param.")
    public ResponseEntity<UserResponse> getProfile(@RequestParam(required = false) String userId, Authentication authentication) {
        String targetId = (userId != null && !userId.isBlank()) ? userId : (authentication != null ? (String) authentication.getPrincipal() : null);
        if (targetId == null) {
            return ResponseEntity.badRequest().build();
        }
        UserResponse response = userService.getUserProfile(targetId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/profile")
    @Operation(summary = "Update profile details", description = "Updates full name and phone number for user.")
    public ResponseEntity<UserResponse> updateProfile(@RequestParam(required = false) String userId,
                                                      @Valid @RequestBody UpdateProfileRequest request,
                                                      Authentication authentication) {
        String targetId = (userId != null && !userId.isBlank()) ? userId : (authentication != null ? (String) authentication.getPrincipal() : null);
        if (targetId == null) {
            return ResponseEntity.badRequest().build();
        }
        UserResponse response = userService.updateProfile(targetId, request);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/status")
    @Operation(summary = "Update account status", description = "Admin endpoint to activate, suspend or deactivate user account.")
    public ResponseEntity<UserResponse> updateStatus(@PathVariable("id") String id,
                                                    @Valid @RequestBody UpdateStatusRequest request) {
        UserResponse response = userService.updateAccountStatus(id, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get user by ID", description = "Fetches user profile by user ID.")
    public ResponseEntity<UserResponse> getUserById(@PathVariable("id") String id) {
        UserResponse response = userService.getUserById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(summary = "Get all users", description = "Retrieves list of users with optional role filter (PASSENGER, DRIVER, ADMIN).")
    public ResponseEntity<List<UserResponse>> getAllUsers(@RequestParam(required = false) Role role) {
        List<UserResponse> users = userService.getAllUsers(role);
        return ResponseEntity.ok(users);
    }
}
