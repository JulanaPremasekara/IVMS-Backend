package com.example.demo.dto;

import java.time.LocalDateTime;
import java.util.Map;

public record ValidationErrorResponse(
        String message,
        Map<String, String> errors,
        LocalDateTime timestamp
) {}
