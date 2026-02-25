package com.liftit.workout;

import com.liftit.workout.exception.WorkoutAlreadyCompletedException;
import com.liftit.workout.exception.WorkoutNotFoundException;
import com.liftit.workout.exception.WorkoutOwnershipException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class DefaultWorkoutServiceTest {

    private static final Long USER_ID = 100L;
    private static final Long OTHER_USER_ID = 999L;
    private static final Long WORKOUT_ID = 1L;
    private static final Instant NOW = Instant.parse("2026-01-01T10:00:00Z");

    private WorkoutRepository workoutRepository;
    private DefaultWorkoutService service;

    @BeforeEach
    void setUp() {
        workoutRepository = mock(WorkoutRepository.class);
        service = new DefaultWorkoutService(workoutRepository);
    }

    private Workout buildInProgress() {
        return new Workout(WORKOUT_ID, USER_ID, NOW, null,
                WorkoutStatus.IN_PROGRESS, null, List.of(),
                NOW, USER_ID, NOW, USER_ID);
    }

    private Workout buildCompleted() {
        return new Workout(WORKOUT_ID, USER_ID, NOW, NOW,
                WorkoutStatus.COMPLETED, null, List.of(),
                NOW, USER_ID, NOW, USER_ID);
    }

    private WorkoutExercise buildWorkoutExercise() {
        return new WorkoutExercise(0L, 10L, 1, List.of(), null);
    }

    // --- constructor ---

    @Test
    void shouldThrowWhenRepositoryIsNull() {
        assertThrows(IllegalArgumentException.class,
                () -> new DefaultWorkoutService(null));
    }

    // --- start ---

    @Test
    void shouldStartNewWorkoutForUser() {
        // Given
        Workout saved = buildInProgress();
        when(workoutRepository.save(any(Workout.class))).thenReturn(saved);

        // When
        Workout result = service.start(USER_ID, null);

        // Then
        assertEquals(WORKOUT_ID, result.id());
        assertEquals(USER_ID, result.userId());
        assertEquals(WorkoutStatus.IN_PROGRESS, result.status());
        verify(workoutRepository).save(any(Workout.class));
    }

    @Test
    void shouldStartWorkoutWithNotes() {
        // Given
        Workout saved = new Workout(WORKOUT_ID, USER_ID, NOW, null,
                WorkoutStatus.IN_PROGRESS, "leg day", List.of(), NOW, USER_ID, NOW, USER_ID);
        when(workoutRepository.save(any(Workout.class))).thenReturn(saved);

        // When
        Workout result = service.start(USER_ID, "leg day");

        // Then
        assertEquals("leg day", result.notes());
    }

    @Test
    void shouldThrowWhenStartUserIdIsNull() {
        assertThrows(IllegalArgumentException.class, () -> service.start(null, null));
    }

    // --- getById ---

    @Test
    void shouldReturnWorkoutWhenFoundById() {
        // Given
        Workout workout = buildInProgress();
        when(workoutRepository.findById(WORKOUT_ID)).thenReturn(Optional.of(workout));

        // When
        Workout result = service.getById(WORKOUT_ID);

        // Then
        assertEquals(WORKOUT_ID, result.id());
    }

    @Test
    void shouldThrowWorkoutNotFoundWhenIdDoesNotExist() {
        // Given
        when(workoutRepository.findById(WORKOUT_ID)).thenReturn(Optional.empty());

        // When / Then
        assertThrows(WorkoutNotFoundException.class, () -> service.getById(WORKOUT_ID));
    }

    @Test
    void shouldThrowWhenGetByIdIsNull() {
        assertThrows(IllegalArgumentException.class, () -> service.getById(null));
    }

    // --- listByUser ---

    @Test
    void shouldReturnPagedWorkoutsForUser() {
        // Given
        Workout workout = buildInProgress();
        PageRequest pageable = PageRequest.of(0, 10);
        Page<Workout> page = new PageImpl<>(List.of(workout), pageable, 1);
        when(workoutRepository.findByUserId(USER_ID, pageable)).thenReturn(page);

        // When
        Page<Workout> result = service.listByUser(USER_ID, pageable);

        // Then
        assertEquals(1, result.getTotalElements());
        assertEquals(WORKOUT_ID, result.getContent().getFirst().id());
    }

    @Test
    void shouldThrowWhenListByUserIdIsNull() {
        assertThrows(IllegalArgumentException.class,
                () -> service.listByUser(null, PageRequest.of(0, 10)));
    }

    @Test
    void shouldThrowWhenListByUserPageableIsNull() {
        assertThrows(IllegalArgumentException.class,
                () -> service.listByUser(USER_ID, null));
    }

    // --- addExercise ---

    @Test
    void shouldAddExerciseToInProgressWorkout() {
        // Given
        Workout workout = buildInProgress();
        WorkoutExercise exercise = buildWorkoutExercise();
        Workout updated = workout.withExercise(exercise);
        when(workoutRepository.findById(WORKOUT_ID)).thenReturn(Optional.of(workout));
        when(workoutRepository.save(any(Workout.class))).thenReturn(updated);

        // When
        Workout result = service.addExercise(WORKOUT_ID, exercise, USER_ID);

        // Then
        assertNotNull(result);
        verify(workoutRepository).save(any(Workout.class));
    }

    @Test
    void shouldThrowNotFoundWhenAddingExerciseToMissingWorkout() {
        // Given
        when(workoutRepository.findById(WORKOUT_ID)).thenReturn(Optional.empty());

        // When / Then
        assertThrows(WorkoutNotFoundException.class,
                () -> service.addExercise(WORKOUT_ID, buildWorkoutExercise(), USER_ID));
        verify(workoutRepository, never()).save(any());
    }

    @Test
    void shouldThrowOwnershipWhenAddingExerciseToOtherUsersWorkout() {
        // Given
        Workout workout = buildInProgress();
        when(workoutRepository.findById(WORKOUT_ID)).thenReturn(Optional.of(workout));

        // When / Then
        assertThrows(WorkoutOwnershipException.class,
                () -> service.addExercise(WORKOUT_ID, buildWorkoutExercise(), OTHER_USER_ID));
        verify(workoutRepository, never()).save(any());
    }

    @Test
    void shouldThrowAlreadyCompletedWhenAddingExerciseToCompletedWorkout() {
        // Given
        Workout workout = buildCompleted();
        when(workoutRepository.findById(WORKOUT_ID)).thenReturn(Optional.of(workout));

        // When / Then
        assertThrows(WorkoutAlreadyCompletedException.class,
                () -> service.addExercise(WORKOUT_ID, buildWorkoutExercise(), USER_ID));
        verify(workoutRepository, never()).save(any());
    }

    @Test
    void shouldThrowWhenAddExerciseWorkoutIdIsNull() {
        assertThrows(IllegalArgumentException.class,
                () -> service.addExercise(null, buildWorkoutExercise(), USER_ID));
    }

    @Test
    void shouldThrowWhenAddExerciseExerciseIsNull() {
        assertThrows(IllegalArgumentException.class,
                () -> service.addExercise(WORKOUT_ID, null, USER_ID));
    }

    // --- complete ---

    @Test
    void shouldCompleteInProgressWorkout() {
        // Given
        Workout workout = buildInProgress();
        Workout completed = buildCompleted();
        when(workoutRepository.findById(WORKOUT_ID)).thenReturn(Optional.of(workout));
        when(workoutRepository.save(any(Workout.class))).thenReturn(completed);

        // When
        Workout result = service.complete(WORKOUT_ID, USER_ID);

        // Then
        assertEquals(WorkoutStatus.COMPLETED, result.status());
        verify(workoutRepository).save(any(Workout.class));
    }

    @Test
    void shouldThrowNotFoundWhenCompletingMissingWorkout() {
        // Given
        when(workoutRepository.findById(WORKOUT_ID)).thenReturn(Optional.empty());

        // When / Then
        assertThrows(WorkoutNotFoundException.class,
                () -> service.complete(WORKOUT_ID, USER_ID));
        verify(workoutRepository, never()).save(any());
    }

    @Test
    void shouldThrowOwnershipWhenCompletingOtherUsersWorkout() {
        // Given
        Workout workout = buildInProgress();
        when(workoutRepository.findById(WORKOUT_ID)).thenReturn(Optional.of(workout));

        // When / Then
        assertThrows(WorkoutOwnershipException.class,
                () -> service.complete(WORKOUT_ID, OTHER_USER_ID));
        verify(workoutRepository, never()).save(any());
    }

    @Test
    void shouldThrowAlreadyCompletedWhenCompletingCompletedWorkout() {
        // Given
        Workout workout = buildCompleted();
        when(workoutRepository.findById(WORKOUT_ID)).thenReturn(Optional.of(workout));

        // When / Then
        assertThrows(WorkoutAlreadyCompletedException.class,
                () -> service.complete(WORKOUT_ID, USER_ID));
        verify(workoutRepository, never()).save(any());
    }

    // --- delete ---

    @Test
    void shouldDeleteWorkoutWhenOwner() {
        // Given
        Workout workout = buildInProgress();
        when(workoutRepository.findById(WORKOUT_ID)).thenReturn(Optional.of(workout));

        // When
        service.delete(WORKOUT_ID, USER_ID);

        // Then
        verify(workoutRepository).delete(WORKOUT_ID);
    }

    @Test
    void shouldThrowNotFoundWhenDeletingMissingWorkout() {
        // Given
        when(workoutRepository.findById(WORKOUT_ID)).thenReturn(Optional.empty());

        // When / Then
        assertThrows(WorkoutNotFoundException.class,
                () -> service.delete(WORKOUT_ID, USER_ID));
        verify(workoutRepository, never()).delete(any());
    }

    @Test
    void shouldThrowOwnershipWhenDeletingOtherUsersWorkout() {
        // Given
        Workout workout = buildInProgress();
        when(workoutRepository.findById(WORKOUT_ID)).thenReturn(Optional.of(workout));

        // When / Then
        assertThrows(WorkoutOwnershipException.class,
                () -> service.delete(WORKOUT_ID, OTHER_USER_ID));
        verify(workoutRepository, never()).delete(any());
    }

    @Test
    void shouldThrowWhenDeleteWorkoutIdIsNull() {
        assertThrows(IllegalArgumentException.class, () -> service.delete(null, USER_ID));
    }

    @Test
    void shouldThrowWhenDeleteUserIdIsNull() {
        assertThrows(IllegalArgumentException.class, () -> service.delete(WORKOUT_ID, null));
    }
}
