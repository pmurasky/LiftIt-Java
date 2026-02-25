package com.liftit.exercise;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.ApplicationArguments;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ExerciseCategoryEnumValidatorTest {

    private static final Instant NOW = Instant.parse("2026-01-01T00:00:00Z");
    private static final Long SYSTEM_USER_ID = 1L;

    @Mock
    private ExerciseCategoryRepository exerciseCategoryRepository;

    private ExerciseCategoryEnumValidator validator;

    @BeforeEach
    void setUp() {
        validator = new ExerciseCategoryEnumValidator(exerciseCategoryRepository);
    }

    @Test
    void shouldPassWhenEnumAndTableAreInSync() {
        // Given — STRENGTH in both enum and DB
        when(exerciseCategoryRepository.findAll()).thenReturn(strengthCategory());

        // When / Then
        assertDoesNotThrow(() -> validator.run(mock(ApplicationArguments.class)));
    }

    @Test
    void shouldThrowWhenTableIsEmpty() {
        // Given
        when(exerciseCategoryRepository.findAll()).thenReturn(List.of());

        // When / Then
        assertThrows(IllegalStateException.class,
                () -> validator.run(mock(ApplicationArguments.class)));
    }

    @Test
    void shouldThrowWhenTableHasMoreRowsThanEnum() {
        // Given — extra category with id=2 not in the enum
        List<ExerciseCategory> extra = new ArrayList<>(strengthCategory());
        extra.add(new ExerciseCategory(2L, "Cardio", NOW, SYSTEM_USER_ID, NOW, SYSTEM_USER_ID));
        when(exerciseCategoryRepository.findAll()).thenReturn(extra);

        // When / Then
        assertThrows(IllegalStateException.class,
                () -> validator.run(mock(ApplicationArguments.class)));
    }

    @Test
    void shouldThrowWhenTableHasWrongIds() {
        // Given — id=99 instead of id=1
        when(exerciseCategoryRepository.findAll()).thenReturn(
                List.of(new ExerciseCategory(99L, "Unknown", NOW, SYSTEM_USER_ID, NOW, SYSTEM_USER_ID))
        );

        // When / Then
        assertThrows(IllegalStateException.class,
                () -> validator.run(mock(ApplicationArguments.class)));
    }

    private List<ExerciseCategory> strengthCategory() {
        return List.of(new ExerciseCategory(1L, "Strength", NOW, SYSTEM_USER_ID, NOW, SYSTEM_USER_ID));
    }
}
