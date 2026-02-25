package com.liftit.exercise;

import com.liftit.muscle.MuscleEnum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.Set;

/**
 * Request body for {@code POST /api/v1/exercises}.
 *
 * <p>All fields except muscle groups are validated by Bean Validation.
 * The {@code muscleGroups} field must contain at least one muscle group.
 *
 * @param name         the exercise name; must not be blank
 * @param category     the exercise category; must not be null
 * @param muscleGroups the targeted muscle groups; must not be empty
 */
public record CreateExerciseRequest(
        @NotBlank(message = "name must not be blank") String name,
        @NotNull(message = "category must not be null") ExerciseCategoryEnum category,
        @NotEmpty(message = "muscleGroups must not be empty") Set<MuscleEnum> muscleGroups
) {
}
