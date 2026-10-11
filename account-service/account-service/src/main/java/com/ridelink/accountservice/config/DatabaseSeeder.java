package com.ridelink.accountservice.config;

import com.ridelink.accountservice.model.AccountStatus;
import com.ridelink.accountservice.model.Role;
import com.ridelink.accountservice.model.User;
import com.ridelink.accountservice.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class DatabaseSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DatabaseSeeder(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.count() == 0) {
            User passenger = User.builder()
                    .fullName("John Passenger")
                    .email("passenger@ridelink.com")
                    .password(passwordEncoder.encode("password123"))
                    .phone("0771234567")
                    .role(Role.PASSENGER)
                    .status(AccountStatus.ACTIVE)
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();

            User driver = User.builder()
                    .fullName("Sam Driver")
                    .email("driver@ridelink.com")
                    .password(passwordEncoder.encode("password123"))
                    .phone("0779876543")
                    .role(Role.DRIVER)
                    .status(AccountStatus.ACTIVE)
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();

            User admin = User.builder()
                    .fullName("System Admin")
                    .email("admin@ridelink.com")
                    .password(passwordEncoder.encode("admin123"))
                    .phone("0770000000")
                    .role(Role.ADMIN)
                    .status(AccountStatus.ACTIVE)
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();

            userRepository.save(passenger);
            userRepository.save(driver);
            userRepository.save(admin);
            System.out.println(">>> [Account Service] Seeded default users into ridelink_account_db!");
        }
    }
}
