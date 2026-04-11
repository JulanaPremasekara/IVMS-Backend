package org.example.ivms.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class CreateUserRequest {
    @NotBlank(message = "Full name is required")
    private String fullName;

    @NotBlank @Email
    private String email;

    @NotBlank(message = "Password is required")
    private String password;

    @NotBlank
    private String role;

    private boolean forcePasswordChange = true;

    private boolean sendWelcomeEmail = true;
}