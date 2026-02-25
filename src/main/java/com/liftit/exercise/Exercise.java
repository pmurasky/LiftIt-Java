package com.liftit.exercise;

import com.liftit.muscle.MuscleEnum;

import java.time.Instant;
import java.util.Set;

/**
 * Domain model representing an exercise.
 *
 * <p>An exercise has a name, a category (e.g. strength), and targets one or more
 * muscle groups. All fields are immutable; muscle groups are stored as a defensive
 * copy to prevent external mutation.
 *
 * <p>All fields include standard audit columns ({@code created_at}, {@code created_by},
 * {@code updated_at}, {@code updated_by}) as required by the architecture.
 * The {@code createdBy} and {@code updatedBy} fields reference {@code users.id}.
 *
 * <p>Use {@code 0L} for {@code id} when creating a new (unsaved) exercise; the
 * persistence layer maps {@code 0L} to {@code null} so the database identity
 * column assigns the real PK.
 */
public record Exercise(
        Long id,
        String name,
        ExerciseCategoryEnum category,
        Set<MuscleEnum> muscleGroups,
        Instant createdAt,
        Long createdBy,
        Instant updatedAt,
        Long updatedBy
) {
    /**
     * Compact canonical constructor — validates all required fields and defensively
     * copies the muscle groups set.
     *
     * @throws IllegalArgumentException if any field is null, name is blank,
     *                                  or muscleGroups is empty
     */
    public Exercise {
        requireNonNull(id, "id");
        requireNonBlank(name, "name");
        requireNonNull(category, "category");
        requireNonEmpty(muscleGroups, "muscleGroups");
        muscleGroups = Set.copyOf(muscleGroups);
        requireNonNull(createdAt, "createdAt");
        requireNonNull(createdBy, "createdBy");
        requireNonNull(updatedAt, "updatedAt");
        requireNonNull(updatedBy, "updatedBy");
    }

    /**
     * Returns {@code true} if this exercise targets the given muscle group.
     *
     * @param muscle the muscle group to check; must not be null
     * @return {@code true} if the muscle group is targeted by this exercise
     */
    public boolean targets(MuscleEnum muscle) {
        return muscleGroups.contains(muscle);
    }

    /**
     * Returns {@code true} if this exercise is a strength exercise.
     *
     * @return {@code true} if category is {@link ExerciseCategoryEnum#STRENGTH}
     */
    public boolean isStrengthExercise() {
        return category == ExerciseCategoryEnum.STRENGTH;
    }

    private static void requireNonNull(Object value, String fieldName) {
        if (value == null) {
            throw new IllegalArgumentException("Exercise." + fieldName + " must not be null");
        }
    }

    private static void requireNonBlank(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(
                    "Exercise." + fieldName + " must not be null or blank");
        }
    }

    private static void requireNonEmpty(Set<?> value, String fieldName) {
        if (value == null || value.isEmpty()) {
            throw new IllegalArgumentException(
                    "Exercise." + fieldName + " must not be null or empty");
        }
    }
}
