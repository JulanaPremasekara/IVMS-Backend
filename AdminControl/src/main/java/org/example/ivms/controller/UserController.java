package org.example.ivms.controller;

import org.example.ivms.dto.CreateUserRequest;
import org.example.ivms.dto.UpdateUserRequest;
import org.example.ivms.dto.UserDTO;
import org.example.ivms.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/users")
    public List<UserDTO> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/users/{id}")
    public UserDTO getUser(@PathVariable Long id) {
        return userService.getUserById(id);
    }

    @PostMapping("/users/add")
    public UserDTO createUser(@Valid @RequestBody CreateUserRequest request) {
        return userService.createUser(request);
    }

    @PutMapping("/users/{id}")
    public UserDTO updateUser(@PathVariable Long id, @Valid @RequestBody UpdateUserRequest request) {
        return userService.updateUser(id, request);
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/users/{id}/deactivate")
    public ResponseEntity<Void> deactivateUser(@PathVariable Long id) {
        userService.deactivateUser(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/users/{id}/archive")
    public ResponseEntity<Void> archiveUser(@PathVariable Long id) {
        userService.archiveUser(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/users/{id}/permissions")
    public ResponseEntity<UserDTO> updatePermissions(
            @PathVariable Long id,
            @RequestBody Set<String> permissions) {
        UserDTO updated = userService.updateUserPermissions(id, permissions);
        return ResponseEntity.ok(updated);
    }

    // NEW: Frontend safety check
    @GetMapping("/users/{id}/dependencies")
    public boolean checkDependencies(@PathVariable Long id) {
        return userService.hasDependencies(id);
    }

    @PostMapping("/users/{id}/activate")
    public ResponseEntity<Void> activateUser(@PathVariable Long id) {
        userService.activateUser(id);
        return ResponseEntity.ok().build();
    }
}