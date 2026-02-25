package com.liftit.workout;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Aggregate root for the workout domain.
 *
 * <p>A workout belongs to one user and tracks the exercises performed, their sets,
 * and the overall status. Business rules:
 * <ul>
 *   <li>Exercises may only be added to an {@code IN_PROGRESS} workout.</li>
 *   <li>Once {@code COMPLETED}, the workout is immutable.</li>
 *   <li>Completing a workout sets {@code completedAt} to the current instant.</li>
 * </ul>
 *
 * <p>Use {@code 0L} for {@code id} when creating a new (unsaved) workout; the
 * persistence layer maps {@code 0L} to {@code null} so the database identity
 * column assigns the real PK.
 */
public class Workout {

    private final Long id;
    private final Long userId;
    private final Instant startedAt;
    private final Instant completedAt;
    private final WorkoutStatus status;
    private final String notes;
    private final List<WorkoutExercise> exercises;
    private final Instant createdAt;
    private final Long createdBy;
    private final Instant updatedAt;
    private final Long updatedBy;

    /**
     * Full constructor used by the persistence layer and domain factories.
     *
     * @param id          entity ID; use {@code 0L} for unsaved workouts
     * @param userId      owning user ID; must not be null
     * @param startedAt   when the workout started; must not be null
     * @param completedAt when the workout completed; null if still in progress
     * @param status      current lifecycle status; must not be null
     * @param notes       optional notes; may be null
     * @param exercises   exercises in this workout; must not be null
     * @param createdAt   audit timestamp; must not be null
     * @param createdBy   audit user ID; must not be null
     * @param updatedAt   audit timestamp; must not be null
     * @param updatedBy   audit user ID; must not be null
     */
    public Workout(
            Long id, Long userId,
            Instant startedAt, Instant completedAt,
            WorkoutStatus status, String notes,
            List<WorkoutExercise> exercises,
            Instant createdAt, Long createdBy,
            Instant updatedAt, Long updatedBy) {
        requireNonNull(id, "id");
        requireNonNull(userId, "userId");
        requireNonNull(startedAt, "startedAt");
        requireNonNull(status, "status");
        requireNonNull(exercises, "exercises");
        requireNonNull(createdAt, "createdAt");
        requireNonNull(createdBy, "createdBy");
        requireNonNull(updatedAt, "updatedAt");
        requireNonNull(updatedBy, "updatedBy");
        this.id = id;
        this.userId = userId;
        this.startedAt = startedAt;
        this.completedAt = completedAt;
        this.status = status;
        this.notes = notes;
        this.exercises = new ArrayList<>(exercises);
        this.createdAt = createdAt;
        this.createdBy = createdBy;
        this.updatedAt = updatedAt;
        this.updatedBy = updatedBy;
    }

    public Long id() { return id; }
    public Long userId() { return userId; }
    public Instant startedAt() { return startedAt; }
    public Instant completedAt() { return completedAt; }
    public WorkoutStatus status() { return status; }
    public String notes() { return notes; }
    public List<WorkoutExercise> exercises() { return Collections.unmodifiableList(exercises); }
    public Instant createdAt() { return createdAt; }
    public Long createdBy() { return createdBy; }
    public Instant updatedAt() { return updatedAt; }
    public Long updatedBy() { return updatedBy; }

    /**
     * Returns {@code true} if this workout is in progress.
     *
     * @return {@code true} if status is {@link WorkoutStatus#IN_PROGRESS}
     */
    public boolean isInProgress() { return status == WorkoutStatus.IN_PROGRESS; }

    /**
     * Returns a new {@code Workout} with the given exercise appended.
     *
     * @param exercise the exercise to add; must not be null
     * @return a new workout with the exercise added
     * @throws IllegalStateException if the workout is already completed
     */
    public Workout withExercise(WorkoutExercise exercise) {
        if (exercise == null) {
            throw new IllegalArgumentException("exercise must not be null");
        }
        if (status == WorkoutStatus.COMPLETED) {
            throw new IllegalStateException("Cannot add exercises to a completed workout");
        }
        List<WorkoutExercise> newExercises = new ArrayList<>(exercises);
        newExercises.add(exercise);
        return new Workout(id, userId, startedAt, completedAt, status, notes,
                newExercises, createdAt, createdBy, Instant.now(), updatedBy);
    }

    /**
     * Returns a new {@code Workout} marked as completed.
     *
     * @return a new workout with status {@link WorkoutStatus#COMPLETED}
     * @throws IllegalStateException if the workout is already completed
     */
    public Workout complete() {
        if (status == WorkoutStatus.COMPLETED) {
            throw new IllegalStateException("Workout is already completed");
        }
        Instant now = Instant.now();
        return new Workout(id, userId, startedAt, now, WorkoutStatus.COMPLETED, notes,
                exercises, createdAt, createdBy, now, updatedBy);
    }

    /**
     * Returns the total number of sets across all exercises in this workout.
     *
     * @return total set count
     */
    public int totalSetCount() {
        return exercises.stream().mapToInt(WorkoutExercise::setCount).sum();
    }

    private static void requireNonNull(Object value, String fieldName) {
        if (value == null) {
            throw new IllegalArgumentException("Workout." + fieldName + " must not be null");
        }
    }
}
