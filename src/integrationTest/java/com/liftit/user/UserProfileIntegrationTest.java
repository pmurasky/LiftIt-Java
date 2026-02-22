package com.liftit.user;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration test for the user profile API endpoints.
 *
 * <p>Boots the full Spring context with a real Postgres via Testcontainers and
 * exercises the complete HTTP → controller → service → JPA → database stack.
 * Uses real JPA repositories — no mocks.
 *
 * <p>Each test provisions a fresh user row via the provisioning service and
 * cleans up all non-system rows in {@code @AfterEach} to maintain test isolation.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@Testcontainers
@ActiveProfiles("integrationTest")
class UserProfileIntegrationTest {

    @Container
    @ServiceConnection
    static final PostgreSQLContainer<?> POSTGRES =
            new PostgreSQLContainer<>("postgres:17-alpine");

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private UserProvisioningService userProvisioningService;

    private MockMvc mockMvc;

    private static final String AUTH0_ID = "auth0|integrationprofileuser";

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        userProvisioningService.provision(Auth0Id.of(AUTH0_ID), Email.of("profiletest@example.com"));
    }

    @AfterEach
    void tearDown() {
        jdbcTemplate.update("DELETE FROM user_profiles");
        jdbcTemplate.update("DELETE FROM users WHERE id > 99");
    }

    @Test
    void shouldCreateProfileViaEndpointAndReturn201() throws Exception {
        // Given
        String requestBody = """
                {
                  "username": "integration_user",
                  "displayName": "Integration User",
                  "gender": "prefer_not_to_say",
                  "heightCm": 175.0,
                  "unitsPreference": "metric"
                }
                """;

        // When / Then
        mockMvc.perform(post("/api/v1/users/me/profile")
                        .header("X-Auth0-Id", AUTH0_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("integration_user"))
                .andExpect(jsonPath("$.displayName").value("Integration User"))
                .andExpect(jsonPath("$.unitsPreference").value("metric"))
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.userId").isNumber());
    }

    @Test
    void shouldReturnProfileViaGetEndpointWhenProfileExists() throws Exception {
        // Given — create the profile first
        String requestBody = """
                {
                  "username": "get_user",
                  "unitsPreference": "imperial"
                }
                """;
        mockMvc.perform(post("/api/v1/users/me/profile")
                        .header("X-Auth0-Id", AUTH0_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated());

        // When / Then
        mockMvc.perform(get("/api/v1/users/me/profile")
                        .header("X-Auth0-Id", AUTH0_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("get_user"))
                .andExpect(jsonPath("$.unitsPreference").value("imperial"));
    }

    @Test
    void shouldReturn404ViaGetEndpointWhenNoProfileExists() throws Exception {
        // Given — no profile created for the user

        // When / Then
        mockMvc.perform(get("/api/v1/users/me/profile")
                        .header("X-Auth0-Id", AUTH0_ID))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturn409ViaEndpointWhenUserAlreadyHasProfile() throws Exception {
        // Given — create the profile once
        String firstRequest = """
                {
                  "username": "first_profile",
                  "unitsPreference": "metric"
                }
                """;
        mockMvc.perform(post("/api/v1/users/me/profile")
                        .header("X-Auth0-Id", AUTH0_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(firstRequest))
                .andExpect(status().isCreated());

        // When — try to create a second profile
        String secondRequest = """
                {
                  "username": "second_profile",
                  "unitsPreference": "metric"
                }
                """;

        // Then
        mockMvc.perform(post("/api/v1/users/me/profile")
                        .header("X-Auth0-Id", AUTH0_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(secondRequest))
                .andExpect(status().isConflict());
    }

    @Test
    void shouldReturn401ViaEndpointWhenAuth0IdHeaderIsMissing() throws Exception {
        // Given — no X-Auth0-Id header
        String requestBody = """
                {
                  "username": "ghost_user",
                  "unitsPreference": "metric"
                }
                """;

        // When / Then
        mockMvc.perform(post("/api/v1/users/me/profile")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isUnauthorized());
    }
}
