package com.liftit.exercise;

import com.liftit.muscle.MuscleEnum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.Set;

/**
 * Request body for {@code PUT /api/v1/exercises/{id}}.
 *
 * <p>All fields are required — this is a full replacement (PUT semantics).
 * The authenticated user must be the creator of the exercise.
 *
 * @param name         the updated exercise name; must not be blank
 * @param category     the updated exercise category; must not be null
 * @param muscleGroups the updated targeted muscle groups; must not be empty
 */
public record UpdateExerciseRequest(
        @NotBlank(message = "name must not be blank") String name,
        @NotNull(message = "category must not be null") ExerciseCategoryEnum category,
        @NotEmpty(message = "muscleGroups must not be empty") Set<MuscleEnum> muscleGroups
) {
}
