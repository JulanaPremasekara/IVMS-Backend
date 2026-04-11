package org.example.ivms.service;

import org.example.ivms.dto.CreateUserRequest;
import org.example.ivms.dto.UpdateUserRequest;
import org.example.ivms.dto.UserDTO;
import org.example.ivms.entity.User;
import org.example.ivms.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuditLogService auditLogService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public UserDTO getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "User not found with id: " + id));
        return toDTO(user);
    }

    public UserDTO createUser(CreateUserRequest req) {
        if (userRepository.findByEmail(req.getEmail()).isPresent()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Email already in use: " + req.getEmail());
        }

        User user = new User();
        user.setName(req.getFullName());
        user.setEmail(req.getEmail());
        user.setPassword(passwordEncoder.encode(req.getPassword()));
        user.setRole(req.getRole());
        user.setDesignation(mapRole(req.getRole()));
        user.setUserId(generateUniqueUserId());
        user.setStatus("Active");
        user.setLastLogin(LocalDateTime.now());
        user.setForcePasswordChange(req.isForcePasswordChange());

        User saved = userRepository.save(user);

        auditLogService.log(
                "User",
                "CREATE",
                "User",
                saved.getUserId(),
                "Normal"
        );

        return toDTO(saved);
    }

    public UserDTO updateUser(Long id, UpdateUserRequest req) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "User not found with id: " + id));

        user.setName(req.getFullName());
        user.setRole(req.getRole());
        user.setDesignation(mapRole(req.getRole()));

        User saved = userRepository.save(user);

        auditLogService.log(
                "User",
                "UPDATE",
                "User",
                saved.getUserId(),
                "Normal"
        );

        return toDTO(saved);
    }

    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "User not found with id: " + id));

        String userId = user.getUserId();

        userRepository.delete(user);

        auditLogService.log(
                "User",
                "DELETE",
                "User",
                userId,
                "Normal"
        );
    }

    public void deactivateUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "User not found with id: " + id));

        if ("Inactive".equalsIgnoreCase(user.getStatus())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "User is already deactivated");
        }

        if ("Archived".equalsIgnoreCase(user.getStatus())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Archived users cannot be deactivated");
        }

        user.setStatus("Inactive");
        user.setForcePasswordChange(true);
        userRepository.save(user);

        auditLogService.log(
                "User",
                "DEACTIVATE",
                "User",
                user.getUserId(),
                "Normal"
        );
    }

    public void activateUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "User not found with id: " + id));

        if ("Active".equalsIgnoreCase(user.getStatus())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "User is already active");
        }

        if ("Archived".equalsIgnoreCase(user.getStatus())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Archived users cannot be reactivated");
        }

        user.setStatus("Active");
        user.setForcePasswordChange(true);
        userRepository.save(user);

        auditLogService.log(
                "User",
                "ACTIVATE",
                "User",
                user.getUserId(),
                "Normal"
        );
    }

    public void archiveUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "User not found with id: " + id));

        if ("Archived".equalsIgnoreCase(user.getStatus())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "User is already archived");
        }

        user.setStatus("Archived");
        userRepository.save(user);

        auditLogService.log(
                "User",
                "ARCHIVE",
                "User",
                user.getUserId(),
                "Normal"
        );
    }

    public UserDTO updateUserPermissions(Long id, Set<String> permissions) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "User not found with id: " + id));

        user.setPermissions(permissions);
        User saved = userRepository.save(user);

        auditLogService.log(
                "User",
                "UPDATE_PERMISSIONS",
                "User",
                saved.getUserId(),
                "Normal"
        );

        return toDTO(saved);
    }

    public boolean hasDependencies(Long userId) {
        // Placeholder – implement real dependency checks later
        return false;
    }

    private String generateUniqueUserId() {
        Integer maxNum = userRepository.findMaxUserIdNumericPart();
        int nextNum = (maxNum == null || maxNum < 1) ? 1 : maxNum + 1;
        String candidate = String.format("USR-%03d", nextNum);

        int attempt = 0;
        while (userRepository.findByUserId(candidate).isPresent() && attempt < 10) {
            nextNum++;
            candidate = String.format("USR-%03d", nextNum);
            attempt++;
        }

        if (attempt >= 10) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Failed to generate unique User ID after 10 attempts"
            );
        }

        return candidate;
    }

    private String mapRole(String role) {
        if (role == null) {
            return null;
        }

        return switch (role.toLowerCase()) {
            case "admin" -> "Administrator";
            case "procurement" -> "Procurement Manager";
            case "warehouse" -> "Warehouse Staff";
            case "finance" -> "Finance Officer";
            default -> role;
        };
    }

    private UserDTO toDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setUserId(user.getUserId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setRole(user.getRole());
        dto.setPermissions(user.getPermissions());
        dto.setDesignation(user.getDesignation());
        dto.setStatus(user.getStatus());
        dto.setLastLogin(user.getLastLogin() != null ? user.getLastLogin().toString() : "");
        dto.setHasDependencies(hasDependencies(user.getId()));
        return dto;
    }
}