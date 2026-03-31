package org.example.ivms.dto;

import lombok.Data;

import java.util.Set;

@Data
public class UserDTO {
    private Long id;
    private String userId;
    private String name;
    private String email;
    private String role;
    private Set<String> permissions;
    private String designation;
    private String status;
    private String lastLogin;
    private boolean hasDependencies; // NEW: for frontend safety
}