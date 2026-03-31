package org.example.ivms.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.ivms.dto.CreateUserRequest;
import org.example.ivms.dto.UpdateUserRequest;
import org.example.ivms.dto.UserDTO;
import org.example.ivms.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
    }

    @Test
    void getAllUsers_shouldReturnList() throws Exception {
        UserDTO dto = new UserDTO();
        dto.setId(1L);
        dto.setUserId("USR-001");
        dto.setName("John Doe");
        dto.setEmail("john@example.com");
        dto.setRole("admin");
        dto.setStatus("Active");

        when(userService.getAllUsers()).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/admin/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].userId").value("USR-001"))
                .andExpect(jsonPath("$[0].name").value("John Doe"));
    }

    @Test
    void createUser_shouldReturnCreatedUser() throws Exception {
        CreateUserRequest req = new CreateUserRequest();
        req.setFullName("John Doe");
        req.setEmail("john@example.com");
        req.setPassword("Pass@123");
        req.setRole("admin");
        req.setForcePasswordChange(true);

        UserDTO dto = new UserDTO();
        dto.setId(1L);
        dto.setUserId("USR-001");
        dto.setName("John Doe");
        dto.setEmail("john@example.com");
        dto.setRole("admin");
        dto.setStatus("Active");

        when(userService.createUser(any(CreateUserRequest.class))).thenReturn(dto);

        mockMvc.perform(post("/api/admin/users/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value("USR-001"))
                .andExpect(jsonPath("$.email").value("john@example.com"))
                .andExpect(jsonPath("$.status").value("Active"));
    }

    @Test
    void createUser_shouldReturnBadRequest_whenValidationFails() throws Exception {
        CreateUserRequest req = new CreateUserRequest();
        req.setEmail("john@example.com");
        req.setPassword("Pass@123");
        req.setRole("admin");

        mockMvc.perform(post("/api/admin/users/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getUser_shouldReturnUser() throws Exception {
        UserDTO dto = new UserDTO();
        dto.setId(1L);
        dto.setUserId("USR-001");
        dto.setName("John Doe");
        dto.setEmail("john@example.com");
        dto.setRole("admin");
        dto.setStatus("Active");

        when(userService.getUserById(1L)).thenReturn(dto);

        mockMvc.perform(get("/api/admin/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value("USR-001"))
                .andExpect(jsonPath("$.name").value("John Doe"));
    }

    @Test
    void updateUser_shouldReturnUpdatedUser() throws Exception {
        UpdateUserRequest req = new UpdateUserRequest();
        req.setFullName("Jane Doe");
        req.setRole("finance");

        UserDTO dto = new UserDTO();
        dto.setId(1L);
        dto.setUserId("USR-001");
        dto.setName("Jane Doe");
        dto.setRole("finance");
        dto.setDesignation("Finance Officer");
        dto.setStatus("Active");

        when(userService.updateUser(eq(1L), any(UpdateUserRequest.class))).thenReturn(dto);

        mockMvc.perform(put("/api/admin/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Jane Doe"))
                .andExpect(jsonPath("$.role").value("finance"))
                .andExpect(jsonPath("$.designation").value("Finance Officer"));
    }

    @Test
    void deleteUser_shouldReturnNoContent() throws Exception {
        doNothing().when(userService).deleteUser(1L);

        mockMvc.perform(delete("/api/admin/users/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void deactivateUser_shouldReturnOk() throws Exception {
        doNothing().when(userService).deactivateUser(1L);

        mockMvc.perform(post("/api/admin/users/1/deactivate"))
                .andExpect(status().isOk());
    }

    @Test
    void activateUser_shouldReturnOk() throws Exception {
        doNothing().when(userService).activateUser(1L);

        mockMvc.perform(post("/api/admin/users/1/activate"))
                .andExpect(status().isOk());
    }

    @Test
    void archiveUser_shouldReturnOk() throws Exception {
        doNothing().when(userService).archiveUser(1L);

        mockMvc.perform(post("/api/admin/users/1/archive"))
                .andExpect(status().isOk());
    }

    @Test
    void updatePermissions_shouldReturnUpdatedPermissions() throws Exception {
        UserDTO dto = new UserDTO();
        dto.setId(1L);
        dto.setUserId("USR-001");
        dto.setPermissions(Set.of("READ_USERS", "WRITE_USERS"));

        when(userService.updateUserPermissions(eq(1L), any())).thenReturn(dto);

        Set<String> permissions = Set.of("READ_USERS", "WRITE_USERS");

        mockMvc.perform(put("/api/admin/users/1/permissions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(permissions)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.permissions").isArray());
    }

    @Test
    void checkDependencies_shouldReturnFalse() throws Exception {
        when(userService.hasDependencies(1L)).thenReturn(false);

        mockMvc.perform(get("/api/admin/users/1/dependencies"))
                .andExpect(status().isOk())
                .andExpect(content().string("false"));
    }
}