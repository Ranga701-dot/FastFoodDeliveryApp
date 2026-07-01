package com.fastfood.userservice.service;

import com.fastfood.userservice.config.JwtProperties;
import com.fastfood.userservice.dto.*;
import com.fastfood.userservice.entity.RefreshToken;
import com.fastfood.userservice.entity.User;
import com.fastfood.userservice.exception.InvalidCredentialsException;
import com.fastfood.userservice.repository.UserRepository;
import com.fastfood.userservice.security.jwt.JwtService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final JwtProperties jwtProperties;

    public AuthService(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder, JwtService jwtService, RefreshTokenService refreshTokenService, JwtProperties jwtProperties) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.refreshTokenService = refreshTokenService;
        this.jwtProperties = jwtProperties;
    }
    public LoginResponse login(LoginRequest request) {
        {
            User user = userRepository.findByEmail(request.getEmail()).orElseThrow(() -> new InvalidCredentialsException("Invalid email or password"));

            if (!user.getActive()) {
                throw new InvalidCredentialsException("Account is locked. Contact support.");
            }

            if (LocalDateTime.now().isAfter(user.getPasswordExpiryDate())) {
                throw new InvalidCredentialsException("Password expired. Please reset password.");
            }

            boolean passwordMatches = passwordEncoder.matches(request.getPassword(), user.getPassword());

            if (!passwordMatches) {
                int attempts = user.getFailedLoginAttempts() + 1;
                user.setFailedLoginAttempts(attempts);
                if (attempts >= 5) {
                    user.setActive(false);
                }
                userRepository.save(user);
                throw new InvalidCredentialsException(
                        "Invalid email or password");
            }

            // Reset failed attempts after successful login
            user.setFailedLoginAttempts(0);
            user.setLastLoginDate(LocalDateTime.now());

            userRepository.save(user);

            // Generate Access Token
            String accessToken = jwtService.generateToken(user.getEmail(), user.getRole());

            // Generate Refresh Token
            RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);

            return new LoginResponse(
                    user.getId(),
                    user.getUsername(),
                    user.getEmail(),
                    user.getRole(),
                    accessToken,
                    refreshToken.getToken(),
                    "Bearer",
                    jwtProperties.getExpiration() / 1000,
                    "Login Successful"
            );
        }
    }

    public RefreshTokenResponse refreshToken(
            RefreshTokenRequest request) {
        RefreshToken refreshToken = refreshTokenService.validateRefreshToken(request.getRefreshToken());

        User user = refreshToken.getUser();
        String accessToken = jwtService.generateToken(user.getEmail(), user.getRole());

        return new RefreshTokenResponse(
                accessToken,
                "Bearer",
                jwtProperties.getExpiration() / 1000
        );
    }

    public LogoutResponse logout(LogoutRequest request) {
        refreshTokenService.revokeRefreshToken(request.getRefreshToken());

        return new LogoutResponse(
                true,
                "Logout Successful");
    }
}
