package com.fastfood.userservice.service;

import com.fastfood.userservice.config.JwtProperties;
import com.fastfood.userservice.dto.*;
import com.fastfood.userservice.entity.RefreshToken;
import com.fastfood.userservice.entity.User;
import com.fastfood.userservice.enums.Role;
import com.fastfood.userservice.enums.UserStatus;
import com.fastfood.userservice.exception.InvalidCredentialsException;
import com.fastfood.userservice.exception.UserAlreadyExistsException;
import com.fastfood.userservice.exception.UserNotFoundException;
import com.fastfood.userservice.repository.UserRepository;
import com.fastfood.userservice.security.jwt.JwtService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder, JwtService jwtService, RefreshTokenService refreshTokenService, JwtProperties jwtProperties) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public RegisterResponse register(RegisterRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new UserAlreadyExistsException("Email already registered");
        }

        User user = new User();

        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.CUSTOMER);
        user.setStatus(UserStatus.ACTIVE);
        user.setEmailVerified(false);
        user.setFailedLoginAttempts(0);

        user.setPasswordUpdatedDate(LocalDateTime.now());
        user.setPasswordExpiryDate(LocalDateTime.now().plusDays(90));

        User savedUser = userRepository.save(user);

        return new RegisterResponse(
                savedUser.getId(),
                savedUser.getUsername(),
                savedUser.getEmail()
        );
    }

    public UserProfileResponse getMyProfile(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return new UserProfileResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getRole().name(),
                user.getStatus(),
                user.getEmailVerified(),
                user.getCreatedAt(),
                user.getLastLoginDate(),
                user.getPasswordUpdatedDate()
        );
    }

    public UserProfileResponse updateMyProfile(String email, UpdateProfileRequest request) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setUsername(request.getUsername());

        User updatedUser = userRepository.save(user);

        return new UserProfileResponse(
                updatedUser.getId(),
                updatedUser.getUsername(),
                updatedUser.getEmail(),
                updatedUser.getRole().name(),
                updatedUser.getStatus(),
                updatedUser.getEmailVerified(),
                updatedUser.getCreatedAt(),
                updatedUser.getLastLoginDate(),
                updatedUser.getPasswordUpdatedDate()
        );
    }

    public UserProfileResponse getUserById(Long id) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        return new UserProfileResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getRole().name(),
                user.getStatus(),
                user.getEmailVerified(),
                user.getCreatedAt(),
                user.getLastLoginDate(),
                user.getPasswordUpdatedDate()
        );
    }

    public MessageResponse updateUserRole(Long id, UpdateRoleRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        user.setRole(request.getRole());

        userRepository.save(user);

        return new MessageResponse(true, "Role updated successfully");
    }

    public MessageResponse deactivateUser(Long id) {

        User user = userRepository.findById(id)
                .orElseThrow(() ->
                        new UserNotFoundException("User not found"));

        // Check if user is already inactive
        if (user.getStatus() == UserStatus.INACTIVE) {
            return new MessageResponse(false, "User is already deactivated");
        }
        // Deactivate the user
        user.setStatus(UserStatus.INACTIVE);
        userRepository.save(user);
        return new MessageResponse(true, "User deactivated successfully");
    }
}