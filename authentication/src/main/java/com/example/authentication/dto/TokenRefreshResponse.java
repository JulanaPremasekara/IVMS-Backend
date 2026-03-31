package com.example.authentication.dto;

public record TokenRefreshResponse(
        String accessToken,
        String refreshToken,
        String tokenType
) {
    // You can add a secondary constructor for convenience
    public TokenRefreshResponse(String accessToken, String refreshToken) {
        this(accessToken, refreshToken, "Bearer");
    }
}
