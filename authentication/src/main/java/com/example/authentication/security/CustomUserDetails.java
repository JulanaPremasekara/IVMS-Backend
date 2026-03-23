package com.example.authentication.security;

import com.example.authentication.model.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.List; // MUST HAVE THIS


public class CustomUserDetails implements UserDetails {
    private Long id;
    private String userId;
    private String email;
    private String Username;
    private boolean forcePasswordChange;

    @JsonIgnore
    private String password;

    private Collection<? extends GrantedAuthority> authorities;

    public CustomUserDetails(Long id, String userId, String email, String password,String Username, boolean forcePasswordChange
                             ,Collection<? extends GrantedAuthority> authorities) {
        this.id = id;
        this.userId = userId;
        this.email = email;
        this.password = password;
        this.Username = Username;
        this.forcePasswordChange = forcePasswordChange;
        this.authorities = authorities;
    }

    public static CustomUserDetails build(User user) {
        // Convert the 'role' string from your DB (e.g., "ROLE_USER") into a List of Authorities
        List<GrantedAuthority> authorities = Collections.singletonList(
                new SimpleGrantedAuthority(user.getRole())
        );

        return new CustomUserDetails(
                user.getId(),
                user.getUserId(),
                user.getEmail(),
                user.getPassword(),
                user.getName(),
                user.isForcePasswordChange(),
                authorities);
    }

    // --- FIX FOR GET ROLE ---
    public String getRole() {
        return authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .findFirst()
                .orElse("ROLE_USER"); // Default fallback
    }

    // --- GETTERS FOR YOUR DTO ---
    public Long getId() { return id; }
    public String getUserId() { return userId; }
    public String getEmail() { return email; }
    public String getName() { return Username; }

    // --- REQUIRED SPRING SECURITY OVERRIDES ---
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() { return password; }

    @Override
    public String getUsername() { return email; }

    @Override
    public boolean isAccountNonExpired() { return true; }

    @Override
    public boolean isAccountNonLocked() { return true; }

    @Override
    public boolean isCredentialsNonExpired() { return true; }

    @Override
    public boolean isEnabled() { return true; }


    public boolean isForcePasswordChange() {
        return forcePasswordChange;
    }
}