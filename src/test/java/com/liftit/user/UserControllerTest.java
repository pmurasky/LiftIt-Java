package com.liftit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.liftit.user.exception.DuplicateProfileException;
import com.liftit.user.exception.DuplicateUserException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class UserControllerTest {

    private MockMvc mockMvc;
    private UserProvisioningService userProvisioningService;
    private UserProfileService userProfileService;
    private UserRepository userRepository;
    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    @BeforeEach
    void setUp() {
        userProvisioningService = mock(UserProvisioningService.class);
        userProfileService = mock(UserProfileService.class);
        userRepository = mock(UserRepository.class);
        UserController controller = new UserController(
                userProvisioningService, userProfileService, userRepository
        );
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    // --- POST /api/v1/users/me (existing provisioning tests) ---

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

    // --- POST /api/v1/users/me/profile ---

    @Test
    void shouldReturn201WithProfileResponseOnSuccessfulProfileCreation() throws Exception {
        // Given
        Auth0Id auth0Id = Auth0Id.of("auth0|abc123");
        User user = new User(100L, auth0Id, Email.of("user@example.com"),
                Instant.now(), 1L, Instant.now(), 1L);
        UserProfile profile = new UserProfile(
                1L, 100L, "alice_lifts", "Alice Smith", "female",
                LocalDate.of(1990, 6, 15), 165.0, "metric",
                Instant.parse("2026-02-21T12:00:00Z"), 1L,
                Instant.parse("2026-02-21T12:00:00Z"), 1L
        );
        when(userRepository.findByAuth0Id(auth0Id)).thenReturn(Optional.of(user));
        when(userProfileService.createProfile(eq(100L), any(CreateUserProfileRequest.class)))
                .thenReturn(profile);
        String requestBody = """
                {
                  "username": "alice_lifts",
                  "displayName": "Alice Smith",
                  "gender": "female",
                  "birthdate": "1990-06-15",
                  "heightCm": 165.0,
                  "unitsPreference": "metric"
                }
                """;

        // When / Then
        mockMvc.perform(post("/api/v1/users/me/profile")
                        .header("X-Auth0-Id", "auth0|abc123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.userId").value(100))
                .andExpect(jsonPath("$.username").value("alice_lifts"))
                .andExpect(jsonPath("$.displayName").value("Alice Smith"))
                .andExpect(jsonPath("$.unitsPreference").value("metric"));
    }

    @Test
    void shouldReturn409WhenUserAlreadyHasProfile() throws Exception {
        // Given
        Auth0Id auth0Id = Auth0Id.of("auth0|abc123");
        User user = new User(100L, auth0Id, Email.of("user@example.com"),
                Instant.now(), 1L, Instant.now(), 1L);
        CreateUserProfileRequest request = new CreateUserProfileRequest(
                "alice_lifts", null, null, null, null, "metric"
        );
        when(userRepository.findByAuth0Id(auth0Id)).thenReturn(Optional.of(user));
        when(userProfileService.createProfile(eq(100L), any(CreateUserProfileRequest.class)))
                .thenThrow(DuplicateProfileException.forUser(100L));

        // When / Then
        mockMvc.perform(post("/api/v1/users/me/profile")
                        .header("X-Auth0-Id", "auth0|abc123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());
    }

    @Test
    void shouldReturn409WhenUsernameAlreadyTaken() throws Exception {
        // Given
        Auth0Id auth0Id = Auth0Id.of("auth0|abc123");
        User user = new User(100L, auth0Id, Email.of("user@example.com"),
                Instant.now(), 1L, Instant.now(), 1L);
        CreateUserProfileRequest request = new CreateUserProfileRequest(
                "taken_name", null, null, null, null, "metric"
        );
        when(userRepository.findByAuth0Id(auth0Id)).thenReturn(Optional.of(user));
        when(userProfileService.createProfile(eq(100L), any(CreateUserProfileRequest.class)))
                .thenThrow(DuplicateProfileException.forUsername("taken_name"));

        // When / Then
        mockMvc.perform(post("/api/v1/users/me/profile")
                        .header("X-Auth0-Id", "auth0|abc123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());
    }

    @Test
    void shouldReturn400WhenUsernameIsMissingOnProfileCreation() throws Exception {
        // Given
        Auth0Id auth0Id = Auth0Id.of("auth0|abc123");
        User user = new User(100L, auth0Id, Email.of("user@example.com"),
                Instant.now(), 1L, Instant.now(), 1L);
        CreateUserProfileRequest request = new CreateUserProfileRequest(
                "", null, null, null, null, "metric"
        );
        when(userRepository.findByAuth0Id(auth0Id)).thenReturn(Optional.of(user));
        when(userProfileService.createProfile(eq(100L), any(CreateUserProfileRequest.class)))
                .thenThrow(new IllegalArgumentException("username must not be blank"));

        // When / Then
        mockMvc.perform(post("/api/v1/users/me/profile")
                        .header("X-Auth0-Id", "auth0|abc123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn401WhenAuth0IdHeaderIsMissingOnProfileCreation() throws Exception {
        // Given — no X-Auth0-Id header supplied
        CreateUserProfileRequest request = new CreateUserProfileRequest(
                "alice_lifts", null, null, null, null, "metric"
        );

        // When / Then
        mockMvc.perform(post("/api/v1/users/me/profile")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldReturn401WhenUserNotFoundForAuth0IdOnProfileCreation() throws Exception {
        // Given — valid header but no matching user row
        when(userRepository.findByAuth0Id(any(Auth0Id.class))).thenReturn(Optional.empty());
        CreateUserProfileRequest request = new CreateUserProfileRequest(
                "ghost_user", null, null, null, null, "metric"
        );

        // When / Then
        mockMvc.perform(post("/api/v1/users/me/profile")
                        .header("X-Auth0-Id", "auth0|unknown")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    // --- GET /api/v1/users/me/profile ---

    @Test
    void shouldReturn200WithProfileResponseWhenProfileExists() throws Exception {
        // Given
        Auth0Id auth0Id = Auth0Id.of("auth0|abc123");
        User user = new User(100L, auth0Id, Email.of("user@example.com"),
                Instant.now(), 1L, Instant.now(), 1L);
        UserProfile profile = new UserProfile(
                1L, 100L, "alice_lifts", null, null, null, null, "metric",
                Instant.parse("2026-02-21T12:00:00Z"), 1L,
                Instant.parse("2026-02-21T12:00:00Z"), 1L
        );
        when(userRepository.findByAuth0Id(auth0Id)).thenReturn(Optional.of(user));
        when(userProfileService.getProfile(100L)).thenReturn(Optional.of(profile));

        // When / Then
        mockMvc.perform(get("/api/v1/users/me/profile")
                        .header("X-Auth0-Id", "auth0|abc123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.userId").value(100))
                .andExpect(jsonPath("$.username").value("alice_lifts"));
    }

    @Test
    void shouldReturn404WhenNoProfileExistsForUser() throws Exception {
        // Given
        Auth0Id auth0Id = Auth0Id.of("auth0|abc123");
        User user = new User(100L, auth0Id, Email.of("user@example.com"),
                Instant.now(), 1L, Instant.now(), 1L);
        when(userRepository.findByAuth0Id(auth0Id)).thenReturn(Optional.of(user));
        when(userProfileService.getProfile(100L)).thenReturn(Optional.empty());

        // When / Then
        mockMvc.perform(get("/api/v1/users/me/profile")
                        .header("X-Auth0-Id", "auth0|abc123"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturn401WhenAuth0IdHeaderIsMissingOnGetProfile() throws Exception {
        // Given — no X-Auth0-Id header
        mockMvc.perform(get("/api/v1/users/me/profile"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldReturn401WhenUserNotFoundForAuth0IdOnGetProfile() throws Exception {
        // Given — valid header but no matching user row
        when(userRepository.findByAuth0Id(any(Auth0Id.class))).thenReturn(Optional.empty());

        // When / Then
        mockMvc.perform(get("/api/v1/users/me/profile")
                        .header("X-Auth0-Id", "auth0|unknown"))
                .andExpect(status().isUnauthorized());
    }
}
