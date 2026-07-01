package com.fastfood.userservice.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {

    //Secret key used to sign JWT tokens
    private String secret;
    //Access Token Expiration (milliseconds)
    private long expiration;
    //Refresh Token Expiration (milliseconds)
    private long refreshExpiration;

    public JwtProperties() {
    }
    public String getSecret() {
        return secret;
    }
    public void setSecret(String secret) {
        this.secret = secret;
    }
    public long getExpiration() {
        return expiration;
    }
    public void setExpiration(long expiration) {
        this.expiration = expiration;
    }
    public long getRefreshExpiration() {
        return refreshExpiration;
    }
    public void setRefreshExpiration(long refreshExpiration) {
        this.refreshExpiration = refreshExpiration;
    }
}