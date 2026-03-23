package com.example.authentication.repository;

import com.example.authentication.model.RefreshToken;
import com.example.authentication.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    // Find a token by the actual UUID string
    Optional<RefreshToken> findByToken(String token);

    // Find the current token for a user (useful for rotation without duplicate entry errors)
    Optional<RefreshToken> findByUser(User user);

    // Useful for logging out: remove all tokens for a specific user
    @Modifying
    int deleteByUser(User user);
}
