package com.ridelink.accountservice.service;

import com.ridelink.accountservice.dto.*;
import com.ridelink.accountservice.model.Role;

import java.util.List;

public interface UserService {
    AuthResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
    UserResponse getUserProfile(String userId);
    UserResponse updateProfile(String userId, UpdateProfileRequest request);
    UserResponse updateAccountStatus(String userId, UpdateStatusRequest request);
    UserResponse getUserById(String userId);
    List<UserResponse> getAllUsers(Role roleFilter);
}
