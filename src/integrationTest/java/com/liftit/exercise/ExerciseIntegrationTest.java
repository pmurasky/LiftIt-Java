package com.liftit.exercise;

import com.liftit.auth.JwtTestTokenFactory;
import com.liftit.user.Auth0Id;
import com.liftit.user.Email;
import com.liftit.user.UserProvisioningService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration tests for the exercise REST API.
 *
 * <p>Boots the full Spring context with a real Postgres via Testcontainers and
 * exercises the complete HTTP → controller → service → JPA → database stack.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@Testcontainers
@ActiveProfiles("integrationTest")
class ExerciseIntegrationTest {

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

    private static final String AUTH0_ID = "auth0|exerciseintegrationuser";
    private static final String OTHER_AUTH0_ID = "auth0|otherexerciseintegrationuser";

    @DynamicPropertySource
    static void registerJwtPublicKey(DynamicPropertyRegistry registry) {
        registry.add("security.jwt.public-key", JwtTestTokenFactory::publicKeyPem);
    }

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build();
        userProvisioningService.provision(Auth0Id.of(AUTH0_ID), Email.of("exercise-test@example.com"));
        userProvisioningService.provision(Auth0Id.of(OTHER_AUTH0_ID), Email.of("other-exercise-test@example.com"));
    }

    @AfterEach
    void tearDown() {
        jdbcTemplate.update("DELETE FROM exercise_muscle_groups WHERE exercise_id >= 100");
        jdbcTemplate.update("DELETE FROM exercises WHERE id >= 100");
        jdbcTemplate.update("DELETE FROM users WHERE id > 99");
    }

    // --- POST /api/v1/exercises ---

    @Test
    void shouldCreateExerciseAndReturn201() throws Exception {
        // Given
        String body = """
                {
                  "name": "Barbell Bench Press",
                  "category": "STRENGTH",
                  "muscleGroups": ["CHEST", "TRICEPS", "SHOULDERS"]
                }
                """;

        // When / Then
        mockMvc.perform(post("/api/v1/exercises")
                        .header("Authorization", JwtTestTokenFactory.bearerToken(AUTH0_ID))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.name").value("Barbell Bench Press"))
                .andExpect(jsonPath("$.category").value("STRENGTH"));
    }

    @Test
    void shouldReturn409WhenCreatingExerciseWithDuplicateName() throws Exception {
        // Given — create the exercise first
        String body = """
                {"name": "Squat", "category": "STRENGTH", "muscleGroups": ["THIGHS"]}
                """;
        mockMvc.perform(post("/api/v1/exercises")
                        .header("Authorization", JwtTestTokenFactory.bearerToken(AUTH0_ID))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated());

        // When — create again with the same name
        // Then
        mockMvc.perform(post("/api/v1/exercises")
                        .header("Authorization", JwtTestTokenFactory.bearerToken(AUTH0_ID))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isConflict());
    }

    @Test
    void shouldReturn400WhenCreatingExerciseWithBlankName() throws Exception {
        // Given
        String body = """
                {"name": "", "category": "STRENGTH", "muscleGroups": ["CHEST"]}
                """;

        // When / Then
        mockMvc.perform(post("/api/v1/exercises")
                        .header("Authorization", JwtTestTokenFactory.bearerToken(AUTH0_ID))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn401WhenCreatingExerciseWithoutToken() throws Exception {
        // Given
        String body = """
                {"name": "Deadlift", "category": "STRENGTH", "muscleGroups": ["BACK"]}
                """;

        // When / Then
        mockMvc.perform(post("/api/v1/exercises")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isUnauthorized());
    }

    // --- GET /api/v1/exercises/{id} ---

    @Test
    void shouldReturnExerciseByIdAndReturn200() throws Exception {
        // Given — create an exercise and extract its ID
        String body = """
                {"name": "Pull Up", "category": "STRENGTH", "muscleGroups": ["BACK", "BICEPS"]}
                """;
        MvcResult created = mockMvc.perform(post("/api/v1/exercises")
                        .header("Authorization", JwtTestTokenFactory.bearerToken(AUTH0_ID))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andReturn();
        String responseBody = created.getResponse().getContentAsString();
        long exerciseId = Long.parseLong(responseBody.replaceAll(".*\"id\":(\\d+).*", "$1"));

        // When / Then
        mockMvc.perform(get("/api/v1/exercises/{id}", exerciseId)
                        .header("Authorization", JwtTestTokenFactory.bearerToken(AUTH0_ID)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(exerciseId))
                .andExpect(jsonPath("$.name").value("Pull Up"));
    }

    @Test
    void shouldReturn404WhenGettingNonExistentExercise() throws Exception {
        // When / Then
        mockMvc.perform(get("/api/v1/exercises/{id}", 999999L)
                        .header("Authorization", JwtTestTokenFactory.bearerToken(AUTH0_ID)))
                .andExpect(status().isNotFound());
    }

    // --- PUT /api/v1/exercises/{id} ---

    @Test
    void shouldUpdateExerciseAndReturn200WhenOwner() throws Exception {
        // Given
        String createBody = """
                {"name": "Overhead Press", "category": "STRENGTH", "muscleGroups": ["SHOULDERS"]}
                """;
        MvcResult created = mockMvc.perform(post("/api/v1/exercises")
                        .header("Authorization", JwtTestTokenFactory.bearerToken(AUTH0_ID))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createBody))
                .andExpect(status().isCreated())
                .andReturn();
        long exerciseId = Long.parseLong(
                created.getResponse().getContentAsString().replaceAll(".*\"id\":(\\d+).*", "$1"));

        String updateBody = """
                {"name": "Barbell Overhead Press", "category": "STRENGTH", "muscleGroups": ["SHOULDERS", "TRICEPS"]}
                """;

        // When / Then
        mockMvc.perform(put("/api/v1/exercises/{id}", exerciseId)
                        .header("Authorization", JwtTestTokenFactory.bearerToken(AUTH0_ID))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Barbell Overhead Press"));
    }

    @Test
    void shouldReturn403WhenUpdatingOtherUsersExercise() throws Exception {
        // Given — create as first user
        String createBody = """
                {"name": "Romanian Deadlift", "category": "STRENGTH", "muscleGroups": ["BACK"]}
                """;
        MvcResult created = mockMvc.perform(post("/api/v1/exercises")
                        .header("Authorization", JwtTestTokenFactory.bearerToken(AUTH0_ID))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createBody))
                .andExpect(status().isCreated())
                .andReturn();
        long exerciseId = Long.parseLong(
                created.getResponse().getContentAsString().replaceAll(".*\"id\":(\\d+).*", "$1"));

        // When — try to update as other user
        String updateBody = """
                {"name": "Stiff Leg Deadlift", "category": "STRENGTH", "muscleGroups": ["BACK"]}
                """;

        // Then
        mockMvc.perform(put("/api/v1/exercises/{id}", exerciseId)
                        .header("Authorization", JwtTestTokenFactory.bearerToken(OTHER_AUTH0_ID))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateBody))
                .andExpect(status().isForbidden());
    }

    // --- DELETE /api/v1/exercises/{id} ---

    @Test
    void shouldDeleteExerciseAndReturn204WhenOwner() throws Exception {
        // Given
        String createBody = """
                {"name": "Dumbbell Curl", "category": "STRENGTH", "muscleGroups": ["BICEPS"]}
                """;
        MvcResult created = mockMvc.perform(post("/api/v1/exercises")
                        .header("Authorization", JwtTestTokenFactory.bearerToken(AUTH0_ID))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createBody))
                .andExpect(status().isCreated())
                .andReturn();
        long exerciseId = Long.parseLong(
                created.getResponse().getContentAsString().replaceAll(".*\"id\":(\\d+).*", "$1"));

        // When / Then
        mockMvc.perform(delete("/api/v1/exercises/{id}", exerciseId)
                        .header("Authorization", JwtTestTokenFactory.bearerToken(AUTH0_ID)))
                .andExpect(status().isNoContent());

        // Verify it's gone
        mockMvc.perform(get("/api/v1/exercises/{id}", exerciseId)
                        .header("Authorization", JwtTestTokenFactory.bearerToken(AUTH0_ID)))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturn403WhenDeletingOtherUsersExercise() throws Exception {
        // Given — create as first user
        String createBody = """
                {"name": "Leg Press", "category": "STRENGTH", "muscleGroups": ["THIGHS"]}
                """;
        MvcResult created = mockMvc.perform(post("/api/v1/exercises")
                        .header("Authorization", JwtTestTokenFactory.bearerToken(AUTH0_ID))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createBody))
                .andExpect(status().isCreated())
                .andReturn();
        long exerciseId = Long.parseLong(
                created.getResponse().getContentAsString().replaceAll(".*\"id\":(\\d+).*", "$1"));

        // When — try to delete as other user
        // Then
        mockMvc.perform(delete("/api/v1/exercises/{id}", exerciseId)
                        .header("Authorization", JwtTestTokenFactory.bearerToken(OTHER_AUTH0_ID)))
                .andExpect(status().isForbidden());
    }

    // --- GET /api/v1/exercises ---

    @Test
    void shouldListExercisesAndReturn200() throws Exception {
        // Given — create two exercises
        String body1 = """
                {"name": "Cable Fly", "category": "STRENGTH", "muscleGroups": ["CHEST"]}
                """;
        String body2 = """
                {"name": "Tricep Pushdown", "category": "STRENGTH", "muscleGroups": ["TRICEPS"]}
                """;
        mockMvc.perform(post("/api/v1/exercises")
                .header("Authorization", JwtTestTokenFactory.bearerToken(AUTH0_ID))
                .contentType(MediaType.APPLICATION_JSON).content(body1));
        mockMvc.perform(post("/api/v1/exercises")
                .header("Authorization", JwtTestTokenFactory.bearerToken(AUTH0_ID))
                .contentType(MediaType.APPLICATION_JSON).content(body2));

        // When / Then
        mockMvc.perform(get("/api/v1/exercises")
                        .header("Authorization", JwtTestTokenFactory.bearerToken(AUTH0_ID)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.totalElements").isNumber());
    }

    @Test
    void shouldFilterExercisesBySearchTerm() throws Exception {
        // Given
        String body = """
                {"name": "Seated Cable Row", "category": "STRENGTH", "muscleGroups": ["BACK"]}
                """;
        mockMvc.perform(post("/api/v1/exercises")
                .header("Authorization", JwtTestTokenFactory.bearerToken(AUTH0_ID))
                .contentType(MediaType.APPLICATION_JSON).content(body));

        // When / Then
        mockMvc.perform(get("/api/v1/exercises")
                        .header("Authorization", JwtTestTokenFactory.bearerToken(AUTH0_ID))
                        .param("search", "cable row"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name").value("Seated Cable Row"));
    }

    // --- GET /api/v1/exercises/categories ---

    @Test
    void shouldReturnCategoriesAndReturn200() throws Exception {
        // When / Then
        mockMvc.perform(get("/api/v1/exercises/categories")
                        .header("Authorization", JwtTestTokenFactory.bearerToken(AUTH0_ID)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].name").value("Strength"));
    }

    // --- GET /api/v1/exercises/muscle-groups ---

    @Test
    void shouldReturnMuscleGroupsAndReturn200() throws Exception {
        // When / Then
        mockMvc.perform(get("/api/v1/exercises/muscle-groups")
                        .header("Authorization", JwtTestTokenFactory.bearerToken(AUTH0_ID)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(10));
    }
}
