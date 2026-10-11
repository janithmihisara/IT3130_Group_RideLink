package com.ridelink.accountservice;

import com.ridelink.accountservice.dto.*;
import com.ridelink.accountservice.exception.ResourceNotFoundException;
import com.ridelink.accountservice.exception.UserAlreadyExistsException;
import com.ridelink.accountservice.model.AccountStatus;
import com.ridelink.accountservice.model.Role;
import com.ridelink.accountservice.model.User;
import com.ridelink.accountservice.repository.UserRepository;
import com.ridelink.accountservice.security.JwtTokenProvider;
import com.ridelink.accountservice.service.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @InjectMocks
    private UserServiceImpl userService;

    private User sampleUser;
    private RegisterRequest registerRequest;

    @BeforeEach
    void setUp() {
        sampleUser = User.builder()
                .id("usr-123")
                .fullName("John Doe")
                .email("john@example.com")
                .password("encoded_pass")
                .phone("0771234567")
                .role(Role.PASSENGER)
                .status(AccountStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .build();

        registerRequest = RegisterRequest.builder()
                .fullName("John Doe")
                .email("john@example.com")
                .password("password123")
                .phone("0771234567")
                .role(Role.PASSENGER)
                .build();
    }

    @Test
    void testRegisterUser_Success() {
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encoded_pass");
        when(userRepository.save(any(User.class))).thenReturn(sampleUser);
        when(jwtTokenProvider.generateToken(anyString(), anyString(), anyString())).thenReturn("mocked_jwt");

        AuthResponse response = userService.register(registerRequest);

        assertNotNull(response);
        assertEquals("usr-123", response.getUserId());
        assertEquals("mocked_jwt", response.getToken());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testRegisterUser_AlreadyExists() {
        when(userRepository.existsByEmail(anyString())).thenReturn(true);

        assertThrows(UserAlreadyExistsException.class, () -> userService.register(registerRequest));
    }

    @Test
    void testGetUserProfile_Success() {
        when(userRepository.findById("usr-123")).thenReturn(Optional.of(sampleUser));

        UserResponse response = userService.getUserProfile("usr-123");

        assertNotNull(response);
        assertEquals("John Doe", response.getFullName());
    }

    @Test
    void testGetUserProfile_NotFound() {
        when(userRepository.findById("non-existent")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.getUserProfile("non-existent"));
    }
}
