package com.liftit.exercise;

import com.liftit.muscle.MuscleEnum;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UpdateExerciseRequestTest {

    private static Validator validator;

    @BeforeAll
    static void setUpValidator() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    void shouldPassValidationWithAllValidFields() {
        // Given
        UpdateExerciseRequest request = new UpdateExerciseRequest(
                "Squat", ExerciseCategoryEnum.STRENGTH, Set.of(MuscleEnum.THIGHS)
        );

        // When
        Set<ConstraintViolation<UpdateExerciseRequest>> violations = validator.validate(request);

        // Then
        assertTrue(violations.isEmpty());
    }

    @Test
    void shouldFailValidationWhenNameIsBlank() {
        // Given
        UpdateExerciseRequest request = new UpdateExerciseRequest(
                "   ", ExerciseCategoryEnum.STRENGTH, Set.of(MuscleEnum.THIGHS)
        );

        // When
        Set<ConstraintViolation<UpdateExerciseRequest>> violations = validator.validate(request);

        // Then
        assertFalse(violations.isEmpty());
    }

    @Test
    void shouldFailValidationWhenCategoryIsNull() {
        // Given
        UpdateExerciseRequest request = new UpdateExerciseRequest(
                "Squat", null, Set.of(MuscleEnum.THIGHS)
        );

        // When
        Set<ConstraintViolation<UpdateExerciseRequest>> violations = validator.validate(request);

        // Then
        assertFalse(violations.isEmpty());
    }

    @Test
    void shouldFailValidationWhenMuscleGroupsIsEmpty() {
        // Given
        UpdateExerciseRequest request = new UpdateExerciseRequest(
                "Squat", ExerciseCategoryEnum.STRENGTH, Set.of()
        );

        // When
        Set<ConstraintViolation<UpdateExerciseRequest>> violations = validator.validate(request);

        // Then
        assertFalse(violations.isEmpty());
    }
}
