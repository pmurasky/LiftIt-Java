package com.liftit.exercise.persistence;

import com.liftit.exercise.ExerciseCategory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JpaExerciseCategoryRepositoryTest {

    private static final Instant NOW = Instant.parse("2026-01-01T00:00:00Z");
    private static final Long SYSTEM_USER_ID = 1L;

    @Mock
    private ExerciseCategoryJpaRepository springDataRepository;

    private JpaExerciseCategoryRepository repository;

    @BeforeEach
    void setUp() {
        repository = new JpaExerciseCategoryRepository(springDataRepository);
    }

    @Test
    void shouldReturnAllCategoriesAsDomainRecords() {
        // Given
        ExerciseCategory strength = new ExerciseCategory(1L, "Strength", NOW, SYSTEM_USER_ID, NOW, SYSTEM_USER_ID);
        List<ExerciseCategoryJpaEntity> entities = List.of(
                ExerciseCategoryJpaEntity.fromDomain(strength)
        );
        when(springDataRepository.findAll()).thenReturn(entities);

        // When
        List<ExerciseCategory> result = repository.findAll();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).id());
        assertEquals("Strength", result.get(0).name());
        verify(springDataRepository).findAll();
    }

    @Test
    void shouldReturnEmptyListWhenNoCategoriesExist() {
        // Given
        when(springDataRepository.findAll()).thenReturn(List.of());

        // When
        List<ExerciseCategory> result = repository.findAll();

        // Then
        assertNotNull(result);
        assertEquals(0, result.size());
        verify(springDataRepository).findAll();
    }
}
