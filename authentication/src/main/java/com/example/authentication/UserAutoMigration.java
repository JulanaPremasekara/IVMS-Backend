package com.example.authentication;

import com.example.authentication.model.User;
import com.example.authentication.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UserAutoMigration implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        System.out.println("--- Running Password Auto-Migration Check ---");
        List<User> users = userRepository.findAll();
        int updated = 0;
        
        for (User user : users) {
            String password = user.getPassword();
            // A BCrypt hash always starts with $2a$ or $2b$ and is 60 characters long.
            if (password != null && !password.startsWith("$2a$") && !password.startsWith("$2b$")) {
                System.out.println("Found plain-text password for user: " + user.getEmail() + " -> Hashing now.");
                String hashedPassword = passwordEncoder.encode(password);
                user.setPassword(hashedPassword);
                userRepository.save(user);
                updated++;
            }
        }
        
        System.out.println("--- Finished Password Auto-Migration Check. Updated " + updated + " users. ---");
    }
}
