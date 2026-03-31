package com.example.authentication.controller;

import com.example.authentication.dto.JwtResponse;
import com.example.authentication.dto.LoginRequest;
import com.example.authentication.dto.TokenRefreshRequest;
import com.example.authentication.dto.TokenRefreshResponse;
import com.example.authentication.model.RefreshToken;
import com.example.authentication.security.CustomUserDetails;
import com.example.authentication.security.JwtUtils;
import com.example.authentication.service.RefreshTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import org.springframework.security.crypto.password.PasswordEncoder;
import com.example.authentication.repository.UserRepository;
import com.example.authentication.model.User;
import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5173"}) // Connects to your Node.js frontend
public class AuthController {

    @Autowired
    AuthenticationManager authenticationManager;
    @Autowired
    JwtUtils jwtUtils;
    @Autowired
    RefreshTokenService refreshTokenService;
    @Autowired
    UserRepository userRepository;
    @Autowired
    PasswordEncoder passwordEncoder;

    @PostMapping("/register-dev")
    public ResponseEntity<?> registerDevUser(@RequestBody java.util.Map<String, String> request) {
        String email = request.get("email");
        if (userRepository.findByEmail(email).isPresent()) {
            return ResponseEntity.badRequest().body(java.util.Map.of("message", "Email already exists"));
        }

        User user = new User();
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(request.get("password")));
        user.setName(request.getOrDefault("name", "Test User"));
        user.setRole(request.getOrDefault("role", "ROLE_USER"));
        user.setUserId(request.getOrDefault("userId", "USR-" + UUID.randomUUID().toString().substring(0, 4)));
        user.setDesignation(request.getOrDefault("designation", "Developer"));
        user.setStatus("Active");

        userRepository.save(user);
        return ResponseEntity.ok(java.util.Map.of("message", "User created successfully with securely hashed password!"));
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.email(), loginRequest.password())
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

            String jwt = jwtUtils.generateJwtToken(userDetails);
            RefreshToken refreshToken = refreshTokenService.createRefreshToken(userDetails.getId());

            return ResponseEntity.ok(new JwtResponse(
                    jwt,                            // 1. accessToken
                    refreshToken.getToken(),        // 2. refreshToken
                    userDetails.getUserId(),        // 3. userId (e.g. "USR001")
                    userDetails.getEmail(),          // 4. email
                    userDetails.getName(),           // 5. name
                    userDetails.getRole(),           // 6. role
                    userDetails.isForcePasswordChange() // 7. forcePasswordChange
            ));
        } catch (AuthenticationException e) {
            return ResponseEntity
                    .status(org.springframework.http.HttpStatus.UNAUTHORIZED)
                    .body(java.util.Map.of("message", "Error: Invalid email or password"));
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestBody TokenRefreshRequest request) {
        try {
            var tokenOpt = refreshTokenService.findByToken(request.refreshToken());
            
            if (tokenOpt.isEmpty()) {
                return ResponseEntity.status(401).body(java.util.Map.of("message", "Refresh token not found"));
            }

            RefreshToken refreshToken = refreshTokenService.verifyExpiration(tokenOpt.get());
            User user = refreshToken.getUser();
            
            String token = jwtUtils.generateTokenFromEmail(user.getEmail());
            // Token Rotation: createRefreshToken automatically deletes the old one
            RefreshToken newRefreshToken = refreshTokenService.createRefreshToken(user.getId());
            
            return ResponseEntity.ok(new TokenRefreshResponse(token, newRefreshToken.getToken()));
        } catch (Exception e) {
            System.err.println("Refresh failed: " + e.getMessage());
            return ResponseEntity.status(401).body(java.util.Map.of("message", e.getMessage() != null ? e.getMessage() : "Refresh error"));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logoutUser(@RequestBody TokenRefreshRequest request) {
        refreshTokenService.deleteByToken(request.refreshToken());
        return ResponseEntity.ok(java.util.Map.of("message", "Log out successful!"));
    }
}
