package com.liftit.workout;

import com.liftit.auth.JwtTestTokenFactory;
import com.liftit.user.Auth0Id;
import com.liftit.user.Email;
import com.liftit.user.UserProvisioningService;
import com.liftit.workout.exception.WorkoutAlreadyCompletedException;
import com.liftit.workout.exception.WorkoutNotFoundException;
import com.liftit.workout.exception.WorkoutOwnershipException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Integration tests for the workout service and persistence layer.
 *
 * <p>Boots the full Spring context with a real PostgreSQL via Testcontainers and
 * exercises the complete service → repository → JPA → database stack.
 */
@SpringBootTest
@Testcontainers
@ActiveProfiles("integrationTest")
class WorkoutServiceIntegrationTest {

    @Container
    @ServiceConnection
    static final PostgreSQLContainer<?> POSTGRES =
            new PostgreSQLContainer<>("postgres:17-alpine");

    @DynamicPropertySource
    static void registerJwtPublicKey(DynamicPropertyRegistry registry) {
        registry.add("security.jwt.public-key", JwtTestTokenFactory::publicKeyPem);
    }

    @Autowired
    private WorkoutService workoutService;

    @Autowired
    private UserProvisioningService userProvisioningService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private static final String AUTH0_ID = "auth0|workoutserviceintegrationuser";
    private static final String OTHER_AUTH0_ID = "auth0|otherworkoutserviceintegrationuser";

    private Long userId;
    private Long otherUserId;
    private Long testExerciseId;

    @BeforeEach
    void setUp() {
        userId = userProvisioningService
                .provision(Auth0Id.of(AUTH0_ID), Email.of("workout-service-test@example.com"))
                .id();
        otherUserId = userProvisioningService
                .provision(Auth0Id.of(OTHER_AUTH0_ID), Email.of("other-workout-service-test@example.com"))
                .id();
        testExerciseId = jdbcTemplate.queryForObject(
                "INSERT INTO exercises (name, category_id, created_at, created_by, updated_at, updated_by) "
                + "VALUES ('Test Exercise', 1, now(), 1, now(), 1) RETURNING id",
                Long.class);
    }

    @AfterEach
    void tearDown() {
        jdbcTemplate.update("DELETE FROM workout_sets");
        jdbcTemplate.update("DELETE FROM workout_exercises");
        jdbcTemplate.update("DELETE FROM workouts");
        jdbcTemplate.update("DELETE FROM exercise_muscle_groups WHERE exercise_id = ?", testExerciseId);
        jdbcTemplate.update("DELETE FROM exercises WHERE id = ?", testExerciseId);
        jdbcTemplate.update("DELETE FROM users WHERE id > 99");
    }

    // --- start ---

    @Test
    void shouldStartWorkoutAndPersistToDatabase() {
        // When
        Workout workout = workoutService.start(userId, null);

        // Then
        assertTrue(workout.id() > 0, "Expected database-assigned ID > 0");
        assertEquals(userId, workout.userId());
        assertEquals(WorkoutStatus.IN_PROGRESS, workout.status());
        assertNotNull(workout.startedAt());
        assertTrue(workout.exercises().isEmpty());
    }

    @Test
    void shouldStartWorkoutWithNotes() {
        // When
        Workout workout = workoutService.start(userId, "Push day");

        // Then
        assertEquals("Push day", workout.notes());
    }

    // --- getById ---

    @Test
    void shouldGetWorkoutByIdAfterPersisting() {
        // Given
        Workout saved = workoutService.start(userId, null);

        // When
        Workout found = workoutService.getById(saved.id());

        // Then
        assertEquals(saved.id(), found.id());
        assertEquals(WorkoutStatus.IN_PROGRESS, found.status());
    }

    @Test
    void shouldThrowWorkoutNotFoundForNonExistentId() {
        assertThrows(WorkoutNotFoundException.class, () -> workoutService.getById(999999L));
    }

    // --- listByUser ---

    @Test
    void shouldListWorkoutsForUser() {
        // Given
        workoutService.start(userId, "Workout 1");
        workoutService.start(userId, "Workout 2");

        // When
        Page<Workout> page = workoutService.listByUser(userId, PageRequest.of(0, 10));

        // Then
        assertEquals(2, page.getTotalElements());
    }

    @Test
    void shouldNotReturnOtherUsersWorkoutsInList() {
        // Given
        workoutService.start(userId, "My Workout");
        workoutService.start(otherUserId, "Other Workout");

        // When
        Page<Workout> page = workoutService.listByUser(userId, PageRequest.of(0, 10));

        // Then
        assertEquals(1, page.getTotalElements());
        assertEquals(userId, page.getContent().getFirst().userId());
    }

    // --- addExercise ---

    @Test
    void shouldAddExerciseToInProgressWorkout() {
        // Given
        Workout workout = workoutService.start(userId, null);
        WorkoutExercise exercise = new WorkoutExercise(0L, testExerciseId, 1, List.of(), null);

        // When
        Workout updated = workoutService.addExercise(workout.id(), exercise, userId);

        // Then
        assertEquals(1, updated.exercises().size());
        assertEquals(testExerciseId, updated.exercises().getFirst().exerciseId());
    }

    @Test
    void shouldPersistExerciseSetsWhenAddingExercise() {
        // Given
        Workout workout = workoutService.start(userId, null);
        Weight weight = new Weight(100.0, WeightUnit.LBS);
        WorkoutSet set = new WorkoutSet(1, 10, weight, 8);
        WorkoutExercise exercise = new WorkoutExercise(0L, testExerciseId, 1, List.of(set), "notes");

        // When
        Workout updated = workoutService.addExercise(workout.id(), exercise, userId);

        // Then
        assertEquals(1, updated.exercises().size());
        WorkoutExercise savedExercise = updated.exercises().getFirst();
        assertEquals(1, savedExercise.sets().size());
        WorkoutSet savedSet = savedExercise.sets().getFirst();
        assertEquals(1, savedSet.setNumber());
        assertEquals(10, savedSet.reps());
        assertEquals(100.0, savedSet.weight().value());
        assertEquals(WeightUnit.LBS, savedSet.weight().unit());
        assertEquals(8, savedSet.rpe());
    }

    @Test
    void shouldThrowOwnershipWhenAddingExerciseToOtherUsersWorkout() {
        // Given
        Workout workout = workoutService.start(userId, null);
        WorkoutExercise exercise = new WorkoutExercise(0L, testExerciseId, 1, List.of(), null);

        // When / Then
        assertThrows(WorkoutOwnershipException.class,
                () -> workoutService.addExercise(workout.id(), exercise, otherUserId));
    }

    @Test
    void shouldThrowAlreadyCompletedWhenAddingExerciseToCompletedWorkout() {
        // Given
        Workout workout = workoutService.start(userId, null);
        workoutService.complete(workout.id(), userId);
        WorkoutExercise exercise = new WorkoutExercise(0L, testExerciseId, 1, List.of(), null);

        // When / Then
        assertThrows(WorkoutAlreadyCompletedException.class,
                () -> workoutService.addExercise(workout.id(), exercise, userId));
    }

    // --- complete ---

    @Test
    void shouldCompleteWorkoutAndPersistCompletedStatus() {
        // Given
        Workout workout = workoutService.start(userId, null);

        // When
        Workout completed = workoutService.complete(workout.id(), userId);

        // Then
        assertEquals(WorkoutStatus.COMPLETED, completed.status());
        assertNotNull(completed.completedAt());

        // Verify persisted
        Workout reloaded = workoutService.getById(workout.id());
        assertEquals(WorkoutStatus.COMPLETED, reloaded.status());
        assertNotNull(reloaded.completedAt());
    }

    @Test
    void shouldThrowOwnershipWhenCompletingOtherUsersWorkout() {
        // Given
        Workout workout = workoutService.start(userId, null);

        // When / Then
        assertThrows(WorkoutOwnershipException.class,
                () -> workoutService.complete(workout.id(), otherUserId));
    }

    @Test
    void shouldThrowAlreadyCompletedWhenCompletingWorkoutTwice() {
        // Given
        Workout workout = workoutService.start(userId, null);
        workoutService.complete(workout.id(), userId);

        // When / Then
        assertThrows(WorkoutAlreadyCompletedException.class,
                () -> workoutService.complete(workout.id(), userId));
    }

    // --- delete ---

    @Test
    void shouldDeleteWorkoutAndVerifyItIsGone() {
        // Given
        Workout workout = workoutService.start(userId, null);
        Long id = workout.id();

        // When
        workoutService.delete(id, userId);

        // Then
        assertThrows(WorkoutNotFoundException.class, () -> workoutService.getById(id));
    }

    @Test
    void shouldThrowOwnershipWhenDeletingOtherUsersWorkout() {
        // Given
        Workout workout = workoutService.start(userId, null);

        // When / Then
        assertThrows(WorkoutOwnershipException.class,
                () -> workoutService.delete(workout.id(), otherUserId));
    }

    @Test
    void shouldThrowNotFoundWhenDeletingNonExistentWorkout() {
        assertThrows(WorkoutNotFoundException.class,
                () -> workoutService.delete(999999L, userId));
    }
}
