package com.liftit.user;

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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration test for the user profile API endpoints.
 *
 * <p>Boots the full Spring context with a real Postgres via Testcontainers.
 * {@link UserRepository} and {@link UserProfileRepository} are mocked because
 * their JPA implementations do not exist yet — that will be addressed when
 * the JPA repository issue is resolved. This test verifies that the controller,
 * service, and Spring MVC wiring are correctly integrated end-to-end through
 * the real application context.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@Testcontainers
class UserProfileIntegrationTest {

    @Container
    @ServiceConnection
    static final PostgreSQLContainer<?> POSTGRES =
            new PostgreSQLContainer<>("postgres:17-alpine");

    @Autowired
    private WebApplicationContext webApplicationContext;

    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean
    private UserProfileRepository userProfileRepository;

    private MockMvc mockMvc;

    private static final String AUTH0_ID = "auth0|integrationprofileuser";
    private static final Long USER_ID = 100L;
    private static final Long PROFILE_ID = 1L;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        Auth0Id auth0Id = Auth0Id.of(AUTH0_ID);
        User user = new User(USER_ID, auth0Id, Email.of("profiletest@example.com"),
                Instant.now(), 1L, Instant.now(), 1L);
        when(userRepository.findByAuth0Id(auth0Id)).thenReturn(Optional.of(user));
    }

    @Test
    void shouldCreateProfileViaEndpointAndReturn201() throws Exception {
        // Given
        UserProfile savedProfile = new UserProfile(
                PROFILE_ID, USER_ID, "integration_user", "Integration User",
                "prefer_not_to_say", null, 175.0, "metric",
                Instant.now(), 1L, Instant.now(), 1L
        );
        when(userProfileRepository.findByUserId(USER_ID)).thenReturn(Optional.empty());
        when(userProfileRepository.findByUsername("integration_user")).thenReturn(Optional.empty());
        when(userProfileRepository.save(any(UserProfile.class))).thenReturn(savedProfile);

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
                .andExpect(jsonPath("$.id").value(PROFILE_ID))
                .andExpect(jsonPath("$.userId").value(USER_ID))
                .andExpect(jsonPath("$.username").value("integration_user"))
                .andExpect(jsonPath("$.displayName").value("Integration User"))
                .andExpect(jsonPath("$.unitsPreference").value("metric"));
    }

    @Test
    void shouldReturnProfileViaGetEndpointWhenProfileExists() throws Exception {
        // Given
        UserProfile profile = new UserProfile(
                PROFILE_ID, USER_ID, "integration_user", null, null, null, null, "imperial",
                Instant.now(), 1L, Instant.now(), 1L
        );
        when(userProfileRepository.findByUserId(USER_ID)).thenReturn(Optional.of(profile));

        // When / Then
        mockMvc.perform(get("/api/v1/users/me/profile")
                        .header("X-Auth0-Id", AUTH0_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(PROFILE_ID))
                .andExpect(jsonPath("$.userId").value(USER_ID))
                .andExpect(jsonPath("$.username").value("integration_user"))
                .andExpect(jsonPath("$.unitsPreference").value("imperial"));
    }

    @Test
    void shouldReturn404ViaGetEndpointWhenNoProfileExists() throws Exception {
        // Given
        when(userProfileRepository.findByUserId(USER_ID)).thenReturn(Optional.empty());

        // When / Then
        mockMvc.perform(get("/api/v1/users/me/profile")
                        .header("X-Auth0-Id", AUTH0_ID))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturn409ViaEndpointWhenUserAlreadyHasProfile() throws Exception {
        // Given
        UserProfile existingProfile = new UserProfile(
                PROFILE_ID, USER_ID, "existing_user", null, null, null, null, "metric",
                Instant.now(), 1L, Instant.now(), 1L
        );
        when(userProfileRepository.findByUserId(USER_ID)).thenReturn(Optional.of(existingProfile));

        String requestBody = """
                {
                  "username": "another_name",
                  "unitsPreference": "metric"
                }
                """;

        // When / Then
        mockMvc.perform(post("/api/v1/users/me/profile")
                        .header("X-Auth0-Id", AUTH0_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
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
