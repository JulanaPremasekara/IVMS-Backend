package com.example.authentication.dto;

import jakarta.validation.constraints.NotBlank;

public record TokenRefreshRequest(
        @NotBlank(message = "Refresh token is required")
        String refreshToken
) {
    // No extra methods needed!
    // Spring uses the constructor automatically to map the JSON.
}
