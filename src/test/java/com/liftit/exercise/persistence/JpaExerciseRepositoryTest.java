package com.liftit.exercise.persistence;

import com.liftit.exercise.Exercise;
import com.liftit.exercise.ExerciseCategoryEnum;
import com.liftit.exercise.ExerciseFilter;
import com.liftit.muscle.MuscleEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JpaExerciseRepositoryTest {

    private static final Long ID = 100L;
    private static final String NAME = "Barbell Squat";
    private static final ExerciseCategoryEnum CATEGORY = ExerciseCategoryEnum.STRENGTH;
    private static final Set<MuscleEnum> MUSCLE_GROUPS = Set.of(MuscleEnum.THIGHS, MuscleEnum.BACK);
    private static final Instant NOW = Instant.parse("2026-01-01T00:00:00Z");
    private static final Long USER_ID = 1L;

    @Mock
    private ExerciseJpaRepository springDataRepository;

    private JpaExerciseRepository repository;

    @BeforeEach
    void setUp() {
        repository = new JpaExerciseRepository(springDataRepository);
    }

    @Test
    void shouldSaveExerciseAndReturnDomain() {
        // Given
        Exercise exercise = new Exercise(0L, NAME, CATEGORY, MUSCLE_GROUPS, NOW, USER_ID, NOW, USER_ID);
        ExerciseJpaEntity savedEntity = ExerciseJpaEntity.fromDomain(
                new Exercise(ID, NAME, CATEGORY, MUSCLE_GROUPS, NOW, USER_ID, NOW, USER_ID));
        when(springDataRepository.save(any(ExerciseJpaEntity.class))).thenReturn(savedEntity);

        // When
        Exercise result = repository.save(exercise);

        // Then
        assertEquals(ID, result.id());
        assertEquals(NAME, result.name());
        assertEquals(CATEGORY, result.category());
        verify(springDataRepository).save(any(ExerciseJpaEntity.class));
    }

    @Test
    void shouldFindExerciseByIdWhenPresent() {
        // Given
        Exercise exercise = new Exercise(ID, NAME, CATEGORY, MUSCLE_GROUPS, NOW, USER_ID, NOW, USER_ID);
        when(springDataRepository.findById(ID))
                .thenReturn(Optional.of(ExerciseJpaEntity.fromDomain(exercise)));

        // When
        Optional<Exercise> result = repository.findById(ID);

        // Then
        assertTrue(result.isPresent());
        assertEquals(ID, result.get().id());
        assertEquals(NAME, result.get().name());
        verify(springDataRepository).findById(ID);
    }

    @Test
    void shouldReturnEmptyWhenExerciseNotFoundById() {
        // Given
        when(springDataRepository.findById(ID)).thenReturn(Optional.empty());

        // When
        Optional<Exercise> result = repository.findById(ID);

        // Then
        assertFalse(result.isPresent());
        verify(springDataRepository).findById(ID);
    }

    @Test
    void shouldFindExerciseByNameWhenPresent() {
        // Given
        Exercise exercise = new Exercise(ID, NAME, CATEGORY, MUSCLE_GROUPS, NOW, USER_ID, NOW, USER_ID);
        when(springDataRepository.findByName(NAME))
                .thenReturn(Optional.of(ExerciseJpaEntity.fromDomain(exercise)));

        // When
        Optional<Exercise> result = repository.findByName(NAME);

        // Then
        assertTrue(result.isPresent());
        assertEquals(NAME, result.get().name());
        verify(springDataRepository).findByName(NAME);
    }

    @Test
    void shouldReturnEmptyWhenExerciseNotFoundByName() {
        // Given
        when(springDataRepository.findByName(NAME)).thenReturn(Optional.empty());

        // When
        Optional<Exercise> result = repository.findByName(NAME);

        // Then
        assertFalse(result.isPresent());
        verify(springDataRepository).findByName(NAME);
    }

    @Test
    void shouldReturnPagedExercisesWhenFindAllCalled() {
        // Given
        Exercise exercise = new Exercise(ID, NAME, CATEGORY, MUSCLE_GROUPS, NOW, USER_ID, NOW, USER_ID);
        ExerciseJpaEntity entity = ExerciseJpaEntity.fromDomain(exercise);
        PageRequest pageable = PageRequest.of(0, 10);
        Page<ExerciseJpaEntity> entityPage = new PageImpl<>(List.of(entity), pageable, 1);
        when(springDataRepository.findAll(any(Specification.class), any(PageRequest.class)))
                .thenReturn(entityPage);

        // When
        Page<Exercise> result = repository.findAll(ExerciseFilter.empty(), pageable);

        // Then
        assertEquals(1, result.getTotalElements());
        assertEquals(ID, result.getContent().getFirst().id());
        verify(springDataRepository).findAll(any(Specification.class), any(PageRequest.class));
    }

    @Test
    void shouldDeleteExerciseById() {
        // When
        repository.delete(ID);

        // Then
        verify(springDataRepository).deleteById(ID);
    }
}
