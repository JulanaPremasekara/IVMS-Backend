package org.example.ivms.service;

import org.example.ivms.dto.CreateUserRequest;
import org.example.ivms.dto.UpdateUserRequest;
import org.example.ivms.dto.UserDTO;
import org.example.ivms.entity.User;
import org.example.ivms.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private AuditLogService auditLogService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setUserId("USR-001");
        user.setName("John Doe");
        user.setEmail("john@example.com");
        user.setPassword("encoded-password");
        user.setRole("admin");
        user.setDesignation("Administrator");
        user.setStatus("Active");
        user.setPermissions(new HashSet<>());
    }

    @Test
    void createUser_shouldCreateSuccessfully() {
        CreateUserRequest req = new CreateUserRequest();
        req.setFullName("John Doe");
        req.setEmail("john@example.com");
        req.setPassword("Pass@123");
        req.setRole("admin");
        req.setForcePasswordChange(true);

        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.empty());
        when(userRepository.findMaxUserIdNumericPart()).thenReturn(0);
        when(userRepository.findByUserId("USR-001")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("Pass@123")).thenReturn("encoded-password");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User savedUser = invocation.getArgument(0);
            savedUser.setId(1L);
            return savedUser;
        });

        UserDTO result = userService.createUser(req);

        assertNotNull(result);
        assertEquals("John Doe", result.getName());
        assertEquals("john@example.com", result.getEmail());
        assertEquals("admin", result.getRole());
        assertEquals("USR-001", result.getUserId());
        assertEquals("Active", result.getStatus());

        verify(auditLogService).log("User", "CREATE", "User", "USR-001", "Normal");
    }

    @Test
    void createUser_shouldThrowWhenEmailAlreadyExists() {
        CreateUserRequest req = new CreateUserRequest();
        req.setFullName("John Doe");
        req.setEmail("john@example.com");
        req.setPassword("Pass@123");
        req.setRole("admin");

        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(user));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> userService.createUser(req));

        assertEquals(400, ex.getStatusCode().value());
        assertTrue(ex.getReason().contains("Email already in use"));
    }

    @Test
    void updateUser_shouldUpdateSuccessfully() {
        UpdateUserRequest req = new UpdateUserRequest();
        req.setFullName("Jane Doe");
        req.setRole("finance");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserDTO result = userService.updateUser(1L, req);

        assertEquals("Jane Doe", result.getName());
        assertEquals("finance", result.getRole());
        assertEquals("Finance Officer", result.getDesignation());

        verify(auditLogService).log("User", "UPDATE", "User", "USR-001", "Normal");
    }

    @Test
    void deactivateUser_shouldSetInactive() {
        user.setStatus("Active");
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        userService.deactivateUser(1L);

        assertEquals("Inactive", user.getStatus());
        assertTrue(user.isForcePasswordChange());
        verify(userRepository).save(user);
        verify(auditLogService).log("User", "DEACTIVATE", "User", "USR-001", "Normal");
    }

    @Test
    void deactivateUser_shouldThrowIfAlreadyInactive() {
        user.setStatus("Inactive");
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> userService.deactivateUser(1L));

        assertEquals(400, ex.getStatusCode().value());
        assertTrue(ex.getReason().contains("already deactivated"));
    }

    @Test
    void activateUser_shouldSetActive() {
        user.setStatus("Inactive");
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        userService.activateUser(1L);

        assertEquals("Active", user.getStatus());
        assertTrue(user.isForcePasswordChange());
        verify(userRepository).save(user);
        verify(auditLogService).log("User", "ACTIVATE", "User", "USR-001", "Normal");
    }

    @Test
    void archiveUser_shouldSetArchived() {
        user.setStatus("Active");
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        userService.archiveUser(1L);

        assertEquals("Archived", user.getStatus());
        verify(userRepository).save(user);
        verify(auditLogService).log("User", "ARCHIVE", "User", "USR-001", "Normal");
    }

    @Test
    void updateUserPermissions_shouldUpdateSuccessfully() {
        Set<String> permissions = Set.of("READ_USERS", "WRITE_USERS");
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserDTO result = userService.updateUserPermissions(1L, permissions);

        assertEquals(permissions, result.getPermissions());
        verify(auditLogService).log("User", "UPDATE_PERMISSIONS", "User", "USR-001", "Normal");
    }

    @Test
    void deleteUser_shouldDeleteSuccessfully() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        userService.deleteUser(1L);

        verify(userRepository).delete(user);
        verify(auditLogService).log("User", "DELETE", "User", "USR-001", "Normal");
    }
}