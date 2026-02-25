package com.liftit.exercise;

import tools.jackson.databind.ObjectMapper;
import com.liftit.GlobalExceptionHandler;
import com.liftit.exercise.exception.DuplicateExerciseException;
import com.liftit.exercise.exception.ExerciseNotFoundException;
import com.liftit.exercise.exception.ExerciseOwnershipException;
import com.liftit.muscle.MuscleEnum;
import com.liftit.user.Auth0Id;
import com.liftit.user.Email;
import com.liftit.user.User;
import com.liftit.user.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ExerciseControllerTest {

    private MockMvc mockMvc;
    private ExerciseService exerciseService;
    private UserRepository userRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final Long USER_ID = 100L;
    private static final Long EXERCISE_ID = 1L;
    private static final String AUTH0_ID = "auth0|testuser";
    private static final String NAME = "Bench Press";
    private static final ExerciseCategoryEnum CATEGORY = ExerciseCategoryEnum.STRENGTH;
    private static final Set<MuscleEnum> MUSCLES = Set.of(MuscleEnum.CHEST, MuscleEnum.TRICEPS);
    private static final Instant NOW = Instant.parse("2026-01-01T00:00:00Z");

    @BeforeEach
    void setUp() {
        exerciseService = mock(ExerciseService.class);
        userRepository = mock(UserRepository.class);
        ExerciseController controller = new ExerciseController(exerciseService, userRepository);
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    private void authenticateAs(String auth0Id) {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(auth0Id, null, List.of()));
    }

    private void stubUserLookup() {
        User user = new User(USER_ID, Auth0Id.of(AUTH0_ID), Email.of("test@example.com"),
                NOW, 1L, NOW, 1L);
        when(userRepository.findByAuth0Id(Auth0Id.of(AUTH0_ID))).thenReturn(Optional.of(user));
    }

    private Exercise buildExercise() {
        return new Exercise(EXERCISE_ID, NAME, CATEGORY, MUSCLES, NOW, USER_ID, NOW, USER_ID);
    }

    // --- POST /api/v1/exercises ---

    @Test
    void shouldReturn201WithExerciseResponseOnCreate() throws Exception {
        // Given
        authenticateAs(AUTH0_ID);
        stubUserLookup();
        Exercise created = buildExercise();
        when(exerciseService.create(any(CreateExerciseRequest.class), eq(USER_ID))).thenReturn(created);
        String body = """
                {"name": "Bench Press", "category": "STRENGTH", "muscleGroups": ["CHEST"]}
                """;

        // When / Then
        mockMvc.perform(post("/api/v1/exercises")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(EXERCISE_ID))
                .andExpect(jsonPath("$.name").value(NAME))
                .andExpect(jsonPath("$.category").value("STRENGTH"));
    }

    @Test
    void shouldReturn409OnCreateWhenNameAlreadyExists() throws Exception {
        // Given
        authenticateAs(AUTH0_ID);
        stubUserLookup();
        when(exerciseService.create(any(), any())).thenThrow(new DuplicateExerciseException(NAME));
        String body = """
                {"name": "Bench Press", "category": "STRENGTH", "muscleGroups": ["CHEST"]}
                """;

        // When / Then
        mockMvc.perform(post("/api/v1/exercises")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isConflict());
    }

    @Test
    void shouldReturn400OnCreateWhenNameIsBlank() throws Exception {
        // Given
        authenticateAs(AUTH0_ID);
        String body = """
                {"name": "", "category": "STRENGTH", "muscleGroups": ["CHEST"]}
                """;

        // When / Then
        mockMvc.perform(post("/api/v1/exercises")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn401OnCreateWhenNotAuthenticated() throws Exception {
        // Given — no authentication in security context
        String body = """
                {"name": "Bench Press", "category": "STRENGTH", "muscleGroups": ["CHEST"]}
                """;

        // When / Then
        mockMvc.perform(post("/api/v1/exercises")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldReturn401OnCreateWhenUserNotFoundForPrincipal() throws Exception {
        // Given
        authenticateAs(AUTH0_ID);
        when(userRepository.findByAuth0Id(any(Auth0Id.class))).thenReturn(Optional.empty());
        String body = """
                {"name": "Bench Press", "category": "STRENGTH", "muscleGroups": ["CHEST"]}
                """;

        // When / Then
        mockMvc.perform(post("/api/v1/exercises")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isUnauthorized());
    }

    // --- GET /api/v1/exercises/{id} ---

    @Test
    void shouldReturn200WithExerciseOnGetById() throws Exception {
        // Given
        authenticateAs(AUTH0_ID);
        when(exerciseService.getById(EXERCISE_ID)).thenReturn(buildExercise());

        // When / Then
        mockMvc.perform(get("/api/v1/exercises/{id}", EXERCISE_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(EXERCISE_ID))
                .andExpect(jsonPath("$.name").value(NAME));
    }

    @Test
    void shouldReturn404OnGetByIdWhenExerciseDoesNotExist() throws Exception {
        // Given
        authenticateAs(AUTH0_ID);
        when(exerciseService.getById(EXERCISE_ID)).thenThrow(new ExerciseNotFoundException(EXERCISE_ID));

        // When / Then
        mockMvc.perform(get("/api/v1/exercises/{id}", EXERCISE_ID))
                .andExpect(status().isNotFound());
    }

    // --- PUT /api/v1/exercises/{id} ---

    @Test
    void shouldReturn200WithUpdatedExerciseOnUpdate() throws Exception {
        // Given
        authenticateAs(AUTH0_ID);
        stubUserLookup();
        Exercise updated = new Exercise(EXERCISE_ID, "Updated Name", CATEGORY, MUSCLES, NOW, USER_ID, NOW, USER_ID);
        when(exerciseService.update(eq(EXERCISE_ID), any(UpdateExerciseRequest.class), eq(USER_ID)))
                .thenReturn(updated);
        String body = """
                {"name": "Updated Name", "category": "STRENGTH", "muscleGroups": ["CHEST"]}
                """;

        // When / Then
        mockMvc.perform(put("/api/v1/exercises/{id}", EXERCISE_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Name"));
    }

    @Test
    void shouldReturn403OnUpdateWhenUserDoesNotOwnExercise() throws Exception {
        // Given
        authenticateAs(AUTH0_ID);
        stubUserLookup();
        when(exerciseService.update(eq(EXERCISE_ID), any(), eq(USER_ID)))
                .thenThrow(new ExerciseOwnershipException(EXERCISE_ID));
        String body = """
                {"name": "Updated Name", "category": "STRENGTH", "muscleGroups": ["CHEST"]}
                """;

        // When / Then
        mockMvc.perform(put("/api/v1/exercises/{id}", EXERCISE_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldReturn404OnUpdateWhenExerciseDoesNotExist() throws Exception {
        // Given
        authenticateAs(AUTH0_ID);
        stubUserLookup();
        when(exerciseService.update(eq(EXERCISE_ID), any(), eq(USER_ID)))
                .thenThrow(new ExerciseNotFoundException(EXERCISE_ID));
        String body = """
                {"name": "Updated Name", "category": "STRENGTH", "muscleGroups": ["CHEST"]}
                """;

        // When / Then
        mockMvc.perform(put("/api/v1/exercises/{id}", EXERCISE_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturn401OnUpdateWhenNotAuthenticated() throws Exception {
        // Given — no auth
        String body = """
                {"name": "Updated Name", "category": "STRENGTH", "muscleGroups": ["CHEST"]}
                """;

        // When / Then
        mockMvc.perform(put("/api/v1/exercises/{id}", EXERCISE_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isUnauthorized());
    }

    // --- DELETE /api/v1/exercises/{id} ---

    @Test
    void shouldReturn204OnDeleteWhenOwner() throws Exception {
        // Given
        authenticateAs(AUTH0_ID);
        stubUserLookup();
        doNothing().when(exerciseService).delete(EXERCISE_ID, USER_ID);

        // When / Then
        mockMvc.perform(delete("/api/v1/exercises/{id}", EXERCISE_ID))
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldReturn403OnDeleteWhenUserDoesNotOwnExercise() throws Exception {
        // Given
        authenticateAs(AUTH0_ID);
        stubUserLookup();
        doThrow(new ExerciseOwnershipException(EXERCISE_ID))
                .when(exerciseService).delete(EXERCISE_ID, USER_ID);

        // When / Then
        mockMvc.perform(delete("/api/v1/exercises/{id}", EXERCISE_ID))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldReturn404OnDeleteWhenExerciseDoesNotExist() throws Exception {
        // Given
        authenticateAs(AUTH0_ID);
        stubUserLookup();
        doThrow(new ExerciseNotFoundException(EXERCISE_ID))
                .when(exerciseService).delete(EXERCISE_ID, USER_ID);

        // When / Then
        mockMvc.perform(delete("/api/v1/exercises/{id}", EXERCISE_ID))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturn401OnDeleteWhenNotAuthenticated() throws Exception {
        mockMvc.perform(delete("/api/v1/exercises/{id}", EXERCISE_ID))
                .andExpect(status().isUnauthorized());
    }

    // --- GET /api/v1/exercises ---

    @Test
    void shouldReturn200WithPagedExercisesOnList() throws Exception {
        // Given
        authenticateAs(AUTH0_ID);
        Exercise exercise = buildExercise();
        Page<Exercise> page = new PageImpl<>(List.of(exercise), PageRequest.of(0, 20), 1);
        when(exerciseService.list(any(ExerciseFilter.class), any())).thenReturn(page);

        // When / Then
        mockMvc.perform(get("/api/v1/exercises"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(EXERCISE_ID))
                .andExpect(jsonPath("$.content[0].name").value(NAME))
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    void shouldReturn200WithFilteredExercisesOnList() throws Exception {
        // Given
        authenticateAs(AUTH0_ID);
        Page<Exercise> page = new PageImpl<>(List.of(), PageRequest.of(0, 20), 0);
        when(exerciseService.list(any(ExerciseFilter.class), any())).thenReturn(page);

        // When / Then
        mockMvc.perform(get("/api/v1/exercises")
                        .param("category", "STRENGTH")
                        .param("search", "bench"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(0));
    }

    // --- GET /api/v1/exercises/categories ---

    @Test
    void shouldReturn200WithCategoriesOnGetCategories() throws Exception {
        // Given
        authenticateAs(AUTH0_ID);
        ExerciseCategory category = new ExerciseCategory(1L, "STRENGTH", NOW, 1L, NOW, 1L);
        when(exerciseService.getCategories()).thenReturn(List.of(category));

        // When / Then
        mockMvc.perform(get("/api/v1/exercises/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("STRENGTH"));
    }

    // --- GET /api/v1/exercises/muscle-groups ---

    @Test
    void shouldReturn200WithMuscleGroupsOnGetMuscleGroups() throws Exception {
        // Given
        authenticateAs(AUTH0_ID);
        when(exerciseService.getMuscleGroups()).thenReturn(List.of(MuscleEnum.values()));

        // When / Then
        mockMvc.perform(get("/api/v1/exercises/muscle-groups"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").value("ABDOMINALS"));
    }
}
