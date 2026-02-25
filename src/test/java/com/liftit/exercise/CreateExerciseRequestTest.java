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

class CreateExerciseRequestTest {

    private static Validator validator;

    @BeforeAll
    static void setUpValidator() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    void shouldPassValidationWithAllValidFields() {
        // Given
        CreateExerciseRequest request = new CreateExerciseRequest(
                "Bench Press", ExerciseCategoryEnum.STRENGTH, Set.of(MuscleEnum.CHEST)
        );

        // When
        Set<ConstraintViolation<CreateExerciseRequest>> violations = validator.validate(request);

        // Then
        assertTrue(violations.isEmpty());
    }

    @Test
    void shouldFailValidationWhenNameIsBlank() {
        // Given
        CreateExerciseRequest request = new CreateExerciseRequest(
                "", ExerciseCategoryEnum.STRENGTH, Set.of(MuscleEnum.CHEST)
        );

        // When
        Set<ConstraintViolation<CreateExerciseRequest>> violations = validator.validate(request);

        // Then
        assertFalse(violations.isEmpty());
    }

    @Test
    void shouldFailValidationWhenCategoryIsNull() {
        // Given
        CreateExerciseRequest request = new CreateExerciseRequest(
                "Bench Press", null, Set.of(MuscleEnum.CHEST)
        );

        // When
        Set<ConstraintViolation<CreateExerciseRequest>> violations = validator.validate(request);

        // Then
        assertFalse(violations.isEmpty());
    }

    @Test
    void shouldFailValidationWhenMuscleGroupsIsEmpty() {
        // Given
        CreateExerciseRequest request = new CreateExerciseRequest(
                "Bench Press", ExerciseCategoryEnum.STRENGTH, Set.of()
        );

        // When
        Set<ConstraintViolation<CreateExerciseRequest>> violations = validator.validate(request);

        // Then
        assertFalse(violations.isEmpty());
    }

    @Test
    void shouldFailValidationWhenMuscleGroupsIsNull() {
        // Given
        CreateExerciseRequest request = new CreateExerciseRequest(
                "Bench Press", ExerciseCategoryEnum.STRENGTH, null
        );

        // When
        Set<ConstraintViolation<CreateExerciseRequest>> violations = validator.validate(request);

        // Then
        assertFalse(violations.isEmpty());
    }
}
