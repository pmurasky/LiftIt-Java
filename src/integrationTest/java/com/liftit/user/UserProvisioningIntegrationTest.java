package com.liftit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.Instant;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration test for the user provisioning flow.
 *
 * <p>Boots the full Spring context with a real Postgres via Testcontainers.
 * The {@link UserRepository} is mocked because the JPA implementation
 * does not exist yet — that will be addressed in the repository issue.
 * This test verifies that the controller, service, and Spring MVC wiring
 * are correctly integrated end-to-end through the real application context.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@Testcontainers
class UserProvisioningIntegrationTest {

    @Container
    @ServiceConnection
    static final PostgreSQLContainer<?> POSTGRES =
            new PostgreSQLContainer<>("postgres:17-alpine");

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserRepository userRepository;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    void shouldProvisionUserViaEndpointAndReturnCreated() throws Exception {
        // Given
        Auth0Id auth0Id = Auth0Id.of("auth0|integrationuser");
        Email email = Email.of("integration@example.com");
        User savedUser = new User(100L, auth0Id, email, Instant.now(), 1L, Instant.now(), 1L);

        when(userRepository.findByAuth0Id(any(Auth0Id.class))).thenReturn(Optional.empty());
        when(userRepository.findByEmail(any(Email.class))).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        ProvisionUserRequest request = new ProvisionUserRequest("auth0|integrationuser", "integration@example.com");

        // When / Then
        mockMvc.perform(post("/api/v1/users/me")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(100))
                .andExpect(jsonPath("$.auth0Id").value("auth0|integrationuser"))
                .andExpect(jsonPath("$.email").value("integration@example.com"));
    }

    @Test
    void shouldReturn409WhenUserAlreadyExistsViaEndpoint() throws Exception {
        // Given
        Auth0Id auth0Id = Auth0Id.of("auth0|existing");
        Email email = Email.of("existing@example.com");
        User existingUser = new User(50L, auth0Id, email, Instant.now(), 1L, Instant.now(), 1L);

        when(userRepository.findByAuth0Id(any(Auth0Id.class))).thenReturn(Optional.of(existingUser));

        ProvisionUserRequest request = new ProvisionUserRequest("auth0|existing", "existing@example.com");

        // When / Then
        mockMvc.perform(post("/api/v1/users/me")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());
    }
}
