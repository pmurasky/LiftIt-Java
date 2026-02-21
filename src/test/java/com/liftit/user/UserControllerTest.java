package com.liftit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.Instant;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class UserControllerTest {

    private MockMvc mockMvc;
    private UserProvisioningService userProvisioningService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        userProvisioningService = mock(UserProvisioningService.class);
        UserController controller = new UserController(userProvisioningService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void shouldReturn201WithUserResponseOnSuccessfulProvisioning() throws Exception {
        // Given
        ProvisionUserRequest request = new ProvisionUserRequest("auth0|abc123", "user@example.com");
        User created = new User(100L, Auth0Id.of("auth0|abc123"), Email.of("user@example.com"),
                Instant.parse("2026-01-01T00:00:00Z"), 1L,
                Instant.parse("2026-01-01T00:00:00Z"), 1L);
        when(userProvisioningService.provision(any(Auth0Id.class), any(Email.class))).thenReturn(created);

        // When / Then
        mockMvc.perform(post("/api/v1/users/me")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(100))
                .andExpect(jsonPath("$.auth0Id").value("auth0|abc123"))
                .andExpect(jsonPath("$.email").value("user@example.com"));
    }

    @Test
    void shouldReturn409WhenAuth0IdOrEmailAlreadyExists() throws Exception {
        // Given
        ProvisionUserRequest request = new ProvisionUserRequest("auth0|duplicate", "dup@example.com");
        when(userProvisioningService.provision(any(Auth0Id.class), any(Email.class)))
                .thenThrow(DuplicateUserException.forAuth0Id(Auth0Id.of("auth0|duplicate")));

        // When / Then
        mockMvc.perform(post("/api/v1/users/me")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());
    }

    @Test
    void shouldReturn400WhenAuth0IdIsMissing() throws Exception {
        // Given — auth0Id is blank
        ProvisionUserRequest request = new ProvisionUserRequest("", "user@example.com");

        // When / Then
        mockMvc.perform(post("/api/v1/users/me")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn400WhenEmailIsMissing() throws Exception {
        // Given — email is blank
        ProvisionUserRequest request = new ProvisionUserRequest("auth0|abc123", "");

        // When / Then
        mockMvc.perform(post("/api/v1/users/me")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}
