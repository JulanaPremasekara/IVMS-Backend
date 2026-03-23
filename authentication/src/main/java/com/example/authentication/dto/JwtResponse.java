package com.example.authentication.dto;

import java.util.List;

public record JwtResponse(
        String accessToken,
        String refreshToken,
        String userId,      // The VARCHAR(10) custom ID from your table
        String email,
        String name,
        String role,
        boolean forcePasswordChange
) {}
