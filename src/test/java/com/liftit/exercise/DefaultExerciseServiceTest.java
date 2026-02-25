package com.liftit.exercise;

import com.liftit.exercise.exception.DuplicateExerciseException;
import com.liftit.exercise.exception.ExerciseNotFoundException;
import com.liftit.exercise.exception.ExerciseOwnershipException;
import com.liftit.muscle.MuscleEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class DefaultExerciseServiceTest {

    private static final Long USER_ID = 100L;
    private static final Long OTHER_USER_ID = 999L;
    private static final Long EXERCISE_ID = 1L;
    private static final String NAME = "Bench Press";
    private static final ExerciseCategoryEnum CATEGORY = ExerciseCategoryEnum.STRENGTH;
    private static final Set<MuscleEnum> MUSCLES = Set.of(MuscleEnum.CHEST, MuscleEnum.TRICEPS);
    private static final Instant NOW = Instant.parse("2026-01-01T00:00:00Z");

    private ExerciseRepository exerciseRepository;
    private ExerciseCategoryRepository categoryRepository;
    private DefaultExerciseService service;

    @BeforeEach
    void setUp() {
        exerciseRepository = mock(ExerciseRepository.class);
        categoryRepository = mock(ExerciseCategoryRepository.class);
        service = new DefaultExerciseService(exerciseRepository, categoryRepository);
    }

    // --- constructor ---

    @Test
    void shouldThrowWhenExerciseRepositoryIsNull() {
        assertThrows(IllegalArgumentException.class,
                () -> new DefaultExerciseService(null, categoryRepository));
    }

    @Test
    void shouldThrowWhenCategoryRepositoryIsNull() {
        assertThrows(IllegalArgumentException.class,
                () -> new DefaultExerciseService(exerciseRepository, null));
    }

    // --- create ---

    @Test
    void shouldCreateExerciseWhenNameIsAvailable() {
        // Given
        CreateExerciseRequest request = new CreateExerciseRequest(NAME, CATEGORY, MUSCLES);
        Exercise saved = new Exercise(EXERCISE_ID, NAME, CATEGORY, MUSCLES, NOW, USER_ID, NOW, USER_ID);
        when(exerciseRepository.findByName(NAME)).thenReturn(Optional.empty());
        when(exerciseRepository.save(any(Exercise.class))).thenReturn(saved);

        // When
        Exercise result = service.create(request, USER_ID);

        // Then
        assertEquals(EXERCISE_ID, result.id());
        assertEquals(NAME, result.name());
        assertEquals(USER_ID, result.createdBy());
        verify(exerciseRepository).save(any(Exercise.class));
    }

    @Test
    void shouldThrowDuplicateExerciseExceptionWhenNameAlreadyExists() {
        // Given
        CreateExerciseRequest request = new CreateExerciseRequest(NAME, CATEGORY, MUSCLES);
        Exercise existing = new Exercise(EXERCISE_ID, NAME, CATEGORY, MUSCLES, NOW, USER_ID, NOW, USER_ID);
        when(exerciseRepository.findByName(NAME)).thenReturn(Optional.of(existing));

        // When / Then
        assertThrows(DuplicateExerciseException.class, () -> service.create(request, USER_ID));
        verify(exerciseRepository, never()).save(any());
    }

    @Test
    void shouldThrowWhenCreateRequestIsNull() {
        assertThrows(IllegalArgumentException.class, () -> service.create(null, USER_ID));
    }

    @Test
    void shouldThrowWhenCreateUserIdIsNull() {
        CreateExerciseRequest request = new CreateExerciseRequest(NAME, CATEGORY, MUSCLES);
        assertThrows(IllegalArgumentException.class, () -> service.create(request, null));
    }

    // --- getById ---

    @Test
    void shouldReturnExerciseWhenFoundById() {
        // Given
        Exercise exercise = new Exercise(EXERCISE_ID, NAME, CATEGORY, MUSCLES, NOW, USER_ID, NOW, USER_ID);
        when(exerciseRepository.findById(EXERCISE_ID)).thenReturn(Optional.of(exercise));

        // When
        Exercise result = service.getById(EXERCISE_ID);

        // Then
        assertEquals(EXERCISE_ID, result.id());
        assertEquals(NAME, result.name());
    }

    @Test
    void shouldThrowExerciseNotFoundExceptionWhenIdDoesNotExist() {
        // Given
        when(exerciseRepository.findById(EXERCISE_ID)).thenReturn(Optional.empty());

        // When / Then
        assertThrows(ExerciseNotFoundException.class, () -> service.getById(EXERCISE_ID));
    }

    @Test
    void shouldThrowWhenGetByIdIsNull() {
        assertThrows(IllegalArgumentException.class, () -> service.getById(null));
    }

    // --- update ---

    @Test
    void shouldUpdateExerciseWhenOwnerAndNameAvailable() {
        // Given
        Exercise existing = new Exercise(EXERCISE_ID, NAME, CATEGORY, MUSCLES, NOW, USER_ID, NOW, USER_ID);
        UpdateExerciseRequest request = new UpdateExerciseRequest("New Name", CATEGORY, Set.of(MuscleEnum.BACK));
        Exercise updated = new Exercise(EXERCISE_ID, "New Name", CATEGORY, Set.of(MuscleEnum.BACK), NOW, USER_ID, NOW, USER_ID);
        when(exerciseRepository.findById(EXERCISE_ID)).thenReturn(Optional.of(existing));
        when(exerciseRepository.findByName("New Name")).thenReturn(Optional.empty());
        when(exerciseRepository.save(any(Exercise.class))).thenReturn(updated);

        // When
        Exercise result = service.update(EXERCISE_ID, request, USER_ID);

        // Then
        assertEquals("New Name", result.name());
        verify(exerciseRepository).save(any(Exercise.class));
    }

    @Test
    void shouldAllowUpdateWithSameName() {
        // Given — updating to the same name is fine (it's the same exercise)
        Exercise existing = new Exercise(EXERCISE_ID, NAME, CATEGORY, MUSCLES, NOW, USER_ID, NOW, USER_ID);
        UpdateExerciseRequest request = new UpdateExerciseRequest(NAME, CATEGORY, Set.of(MuscleEnum.BACK));
        when(exerciseRepository.findById(EXERCISE_ID)).thenReturn(Optional.of(existing));
        when(exerciseRepository.findByName(NAME)).thenReturn(Optional.of(existing));
        when(exerciseRepository.save(any(Exercise.class))).thenReturn(existing);

        // When / Then — should not throw
        service.update(EXERCISE_ID, request, USER_ID);
        verify(exerciseRepository).save(any(Exercise.class));
    }

    @Test
    void shouldThrowOwnershipExceptionWhenUpdatingOtherUsersExercise() {
        // Given
        Exercise existing = new Exercise(EXERCISE_ID, NAME, CATEGORY, MUSCLES, NOW, USER_ID, NOW, USER_ID);
        UpdateExerciseRequest request = new UpdateExerciseRequest("New Name", CATEGORY, MUSCLES);
        when(exerciseRepository.findById(EXERCISE_ID)).thenReturn(Optional.of(existing));

        // When / Then
        assertThrows(ExerciseOwnershipException.class,
                () -> service.update(EXERCISE_ID, request, OTHER_USER_ID));
        verify(exerciseRepository, never()).save(any());
    }

    @Test
    void shouldThrowNotFoundOnUpdateWhenExerciseDoesNotExist() {
        // Given
        when(exerciseRepository.findById(EXERCISE_ID)).thenReturn(Optional.empty());
        UpdateExerciseRequest request = new UpdateExerciseRequest(NAME, CATEGORY, MUSCLES);

        // When / Then
        assertThrows(ExerciseNotFoundException.class,
                () -> service.update(EXERCISE_ID, request, USER_ID));
    }

    @Test
    void shouldThrowDuplicateExerciseExceptionOnUpdateWhenNameTakenByOtherExercise() {
        // Given
        Exercise existing = new Exercise(EXERCISE_ID, NAME, CATEGORY, MUSCLES, NOW, USER_ID, NOW, USER_ID);
        Exercise other = new Exercise(2L, "Other Exercise", CATEGORY, MUSCLES, NOW, USER_ID, NOW, USER_ID);
        UpdateExerciseRequest request = new UpdateExerciseRequest("Other Exercise", CATEGORY, MUSCLES);
        when(exerciseRepository.findById(EXERCISE_ID)).thenReturn(Optional.of(existing));
        when(exerciseRepository.findByName("Other Exercise")).thenReturn(Optional.of(other));

        // When / Then
        assertThrows(DuplicateExerciseException.class,
                () -> service.update(EXERCISE_ID, request, USER_ID));
    }

    // --- delete ---

    @Test
    void shouldDeleteExerciseWhenOwner() {
        // Given
        Exercise existing = new Exercise(EXERCISE_ID, NAME, CATEGORY, MUSCLES, NOW, USER_ID, NOW, USER_ID);
        when(exerciseRepository.findById(EXERCISE_ID)).thenReturn(Optional.of(existing));

        // When
        service.delete(EXERCISE_ID, USER_ID);

        // Then
        verify(exerciseRepository).delete(EXERCISE_ID);
    }

    @Test
    void shouldThrowOwnershipExceptionWhenDeletingOtherUsersExercise() {
        // Given
        Exercise existing = new Exercise(EXERCISE_ID, NAME, CATEGORY, MUSCLES, NOW, USER_ID, NOW, USER_ID);
        when(exerciseRepository.findById(EXERCISE_ID)).thenReturn(Optional.of(existing));

        // When / Then
        assertThrows(ExerciseOwnershipException.class,
                () -> service.delete(EXERCISE_ID, OTHER_USER_ID));
        verify(exerciseRepository, never()).delete(any());
    }

    @Test
    void shouldThrowNotFoundOnDeleteWhenExerciseDoesNotExist() {
        // Given
        when(exerciseRepository.findById(EXERCISE_ID)).thenReturn(Optional.empty());

        // When / Then
        assertThrows(ExerciseNotFoundException.class,
                () -> service.delete(EXERCISE_ID, USER_ID));
    }

    // --- list ---

    @Test
    void shouldReturnPagedExercises() {
        // Given
        Exercise exercise = new Exercise(EXERCISE_ID, NAME, CATEGORY, MUSCLES, NOW, USER_ID, NOW, USER_ID);
        PageRequest pageable = PageRequest.of(0, 10);
        Page<Exercise> page = new PageImpl<>(List.of(exercise), pageable, 1);
        when(exerciseRepository.findAll(any(ExerciseFilter.class), any(PageRequest.class))).thenReturn(page);

        // When
        Page<Exercise> result = service.list(ExerciseFilter.empty(), pageable);

        // Then
        assertEquals(1, result.getTotalElements());
        assertEquals(NAME, result.getContent().getFirst().name());
    }

    @Test
    void shouldThrowWhenListFilterIsNull() {
        assertThrows(IllegalArgumentException.class,
                () -> service.list(null, PageRequest.of(0, 10)));
    }

    // --- getCategories ---

    @Test
    void shouldReturnAllCategories() {
        // Given
        ExerciseCategory category = new ExerciseCategory(1L, "STRENGTH", NOW, 1L, NOW, 1L);
        when(categoryRepository.findAll()).thenReturn(List.of(category));

        // When
        List<ExerciseCategory> result = service.getCategories();

        // Then
        assertEquals(1, result.size());
        assertEquals("STRENGTH", result.getFirst().name());
    }

    // --- getMuscleGroups ---

    @Test
    void shouldReturnAllMuscleGroups() {
        // When
        List<MuscleEnum> result = service.getMuscleGroups();

        // Then
        assertNotNull(result);
        assertEquals(MuscleEnum.values().length, result.size());
    }
}
