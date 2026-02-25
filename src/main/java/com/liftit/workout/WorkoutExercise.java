package com.liftit.workout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Entity within the {@link Workout} aggregate representing one exercise performed
 * during a workout, along with its logged sets.
 *
 * <p>The {@code exerciseId} references the {@link com.liftit.exercise.Exercise} domain.
 * The {@code order} field determines the sequence of exercises within the workout
 * (1-based). A {@code 0L} id indicates an unsaved entity; the persistence layer
 * assigns the real PK.
 */
public class WorkoutExercise {

    private final Long id;
    private final Long exerciseId;
    private final int order;
    private final List<WorkoutSet> sets;
    private final String notes;

    /**
     * Creates a new {@code WorkoutExercise}.
     *
     * @param id         the entity ID; use {@code 0L} for unsaved entities
     * @param exerciseId the referenced exercise ID; must not be null
     * @param order      the 1-based sequence position; must be &gt;= 1
     * @param sets       the sets logged for this exercise; must not be null
     * @param notes      optional notes; may be null
     * @throws IllegalArgumentException if exerciseId is null or order &lt; 1
     */
    public WorkoutExercise(Long id, Long exerciseId, int order, List<WorkoutSet> sets, String notes) {
        if (id == null) {
            throw new IllegalArgumentException("WorkoutExercise.id must not be null");
        }
        if (exerciseId == null) {
            throw new IllegalArgumentException("WorkoutExercise.exerciseId must not be null");
        }
        if (order < 1) {
            throw new IllegalArgumentException("WorkoutExercise.order must be >= 1");
        }
        if (sets == null) {
            throw new IllegalArgumentException("WorkoutExercise.sets must not be null");
        }
        this.id = id;
        this.exerciseId = exerciseId;
        this.order = order;
        this.sets = new ArrayList<>(sets);
        this.notes = notes;
    }

    /** Returns the entity ID. {@code 0L} means unsaved. */
    public Long id() { return id; }

    /** Returns the referenced exercise ID. */
    public Long exerciseId() { return exerciseId; }

    /** Returns the 1-based sequence order of this exercise in the workout. */
    public int order() { return order; }

    /** Returns an unmodifiable view of the logged sets. */
    public List<WorkoutSet> sets() { return Collections.unmodifiableList(sets); }

    /** Returns optional notes for this exercise, or {@code null}. */
    public String notes() { return notes; }

    /**
     * Returns a new {@code WorkoutExercise} with the given set appended.
     *
     * @param set the set to add; must not be null
     * @return a new instance with the set added
     */
    public WorkoutExercise withSet(WorkoutSet set) {
        if (set == null) {
            throw new IllegalArgumentException("set must not be null");
        }
        List<WorkoutSet> newSets = new ArrayList<>(sets);
        newSets.add(set);
        return new WorkoutExercise(id, exerciseId, order, newSets, notes);
    }

    /**
     * Returns the total number of sets logged for this exercise.
     *
     * @return the set count
     */
    public int setCount() { return sets.size(); }
}
