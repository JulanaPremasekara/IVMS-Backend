package org.example.ivms.repository;

import org.example.ivms.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    Optional<User> findByUserId(String userId);

    // FIXED: Use @Query with proper JPQL to extract max numeric part
    @Query("SELECT MAX(CAST(SUBSTRING(u.userId, 5) AS integer)) " +
            "FROM User u " +
            "WHERE u.userId LIKE 'USR-%'")
    Integer findMaxUserIdNumericPart();
}