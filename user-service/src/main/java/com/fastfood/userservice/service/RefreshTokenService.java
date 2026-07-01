package com.fastfood.userservice.service;

import com.fastfood.userservice.entity.RefreshToken;
import com.fastfood.userservice.entity.User;
import com.fastfood.userservice.exception.InvalidRefreshTokenException;
import com.fastfood.userservice.exception.RefreshTokenExpiredException;
import com.fastfood.userservice.repository.RefreshTokenRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    @Value("${jwt.refresh-expiration}")
    private long refreshExpiration;

    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
    }

    //Create a new Refresh Token
    public RefreshToken createRefreshToken(User user) {
        RefreshToken refreshToken = new RefreshToken();

        refreshToken.setUser(user);
        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken.setCreatedAt(LocalDateTime.now());
        refreshToken.setExpiryDate(LocalDateTime.now().plusSeconds(refreshExpiration / 1000));
        refreshToken.setRevoked(false);
        return refreshTokenRepository.save(refreshToken);
    }

    //Find Refresh Token
    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    //Check Expiry
    public boolean isExpired(RefreshToken refreshToken) {
        return LocalDateTime.now().isAfter(refreshToken.getExpiryDate());
    }

    //Revoke Token
    public void revokeToken(RefreshToken refreshToken) {
        refreshToken.setRevoked(true);
        refreshTokenRepository.save(refreshToken);
    }

    //Delete all User Tokens
    public void deleteUserTokens(User user) {
        refreshTokenRepository.deleteByUser(user);
    }

    //Validate Refresh Token
    public RefreshToken validateRefreshToken(String token) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(token).orElseThrow(() -> new RuntimeException("Invalid Refresh Token"));

        if (refreshToken.isRevoked()) {
            throw new InvalidRefreshTokenException("Refresh token has been revoked");
        }
        if (isExpired(refreshToken)) {
            throw new RefreshTokenExpiredException("Refresh token has expired");
        }
        return refreshToken;
    }

    //Save Refresh Token
    public RefreshToken save(RefreshToken refreshToken) {
        return refreshTokenRepository.save(refreshToken);
    }


}