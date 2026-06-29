package com.fastfood.userservice.service;

import com.fastfood.userservice.dto.LoginRequest;
import com.fastfood.userservice.dto.LoginResponse;
import com.fastfood.userservice.dto.RegisterRequest;
import com.fastfood.userservice.dto.RegisterResponse;
import com.fastfood.userservice.entity.User;
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
    private final JwtService jwtService;

    public UserService(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder,JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService=jwtService;
    }

    public RegisterResponse register(RegisterRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new UserAlreadyExistsException(
                    "Email already registered");
        }

        User user = new User();

        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(
                passwordEncoder.encode(request.getPassword())
        );
        user.setRole("CUSTOMER");
        user.setActive(true);
        user.setEmailVerified(false);
        user.setFailedLoginAttempts(0);

        user.setPasswordUpdatedDate(LocalDateTime.now());
        user.setPasswordExpiryDate(
                LocalDateTime.now().plusDays(90)
        );

        User savedUser = userRepository.save(user);

        return new RegisterResponse(
                savedUser.getId(),
                savedUser.getUsername(),
                savedUser.getEmail()
        );
    }

    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() ->
                        new InvalidCredentialsException("Invalid email or password"));
        //jwt token
        String token = jwtService.generateToken(
                user.getEmail(),
                user.getRole()
        );

        if (!user.getActive()) {
            throw new InvalidCredentialsException("Account is locked. Contact support.");
        }

        if (LocalDateTime.now().isAfter(user.getPasswordExpiryDate())) {
            throw new InvalidCredentialsException(
                    "Password expired. Please reset password.");
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
        user.setFailedLoginAttempts(0);
        user.setLastLoginDate(LocalDateTime.now());
        userRepository.save(user);

        return new LoginResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getRole(),
                token,
                "Login Successful"
        );
    }
}