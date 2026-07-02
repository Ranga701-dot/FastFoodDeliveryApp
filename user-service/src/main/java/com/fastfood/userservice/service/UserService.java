package com.fastfood.userservice.service;

import com.fastfood.userservice.config.JwtProperties;
import com.fastfood.userservice.dto.LoginRequest;
import com.fastfood.userservice.dto.LoginResponse;
import com.fastfood.userservice.dto.RegisterRequest;
import com.fastfood.userservice.dto.RegisterResponse;
import com.fastfood.userservice.entity.RefreshToken;
import com.fastfood.userservice.entity.User;
import com.fastfood.userservice.enums.Role;
import com.fastfood.userservice.exception.InvalidCredentialsException;
import com.fastfood.userservice.exception.UserAlreadyExistsException;
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
        user.setActive(true);
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
}