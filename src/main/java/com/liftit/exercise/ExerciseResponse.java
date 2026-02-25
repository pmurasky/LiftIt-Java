package com.liftit.exercise;

import com.liftit.muscle.MuscleEnum;

import java.time.Instant;
import java.util.Set;

/**
 * API response representing an exercise.
 *
 * <p>Returned by all exercise endpoints that produce a response body.
 * Exposes only the fields the frontend needs — internal audit columns
 * ({@code updatedBy}) are not included.
 *
 * @param id           the application-assigned exercise ID
 * @param name         the exercise name
 * @param category     the exercise category
 * @param muscleGroups the set of targeted muscle groups
 * @param createdBy    the ID of the user who created the exercise
 * @param createdAt    the timestamp when the exercise was created
 * @param updatedAt    the timestamp when the exercise was last updated
 */
public record ExerciseResponse(
        Long id,
        String name,
        ExerciseCategoryEnum category,
        Set<MuscleEnum> muscleGroups,
        Long createdBy,
        Instant createdAt,
        Instant updatedAt
) {

    /**
     * Converts an {@link Exercise} domain object to an {@code ExerciseResponse}.
     *
     * @param exercise the domain exercise; must not be null
     * @return a new {@code ExerciseResponse}
     */
    public static ExerciseResponse from(Exercise exercise) {
        return new ExerciseResponse(
                exercise.id(),
                exercise.name(),
                exercise.category(),
                exercise.muscleGroups(),
                exercise.createdBy(),
                exercise.createdAt(),
                exercise.updatedAt()
        );
    }
}
