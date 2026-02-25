package com.liftit.exercise;

import java.util.Optional;

/**
 * Data-access contract for {@link Exercise} entities.
 *
 * <p>Callers depend on this abstraction rather than any concrete implementation
 * (Dependency Inversion Principle). Implementations may store exercises in a
 * relational database, in-memory store, or any other backing mechanism.
 */
public interface ExerciseRepository {

    /**
     * Persists an exercise. If an exercise with the same {@code id} already exists,
     * it is replaced.
     *
     * @param exercise the exercise to save; must not be null
     * @return the saved exercise with its database-assigned ID
     */
    Exercise save(Exercise exercise);

    /**
     * Finds an exercise by its internal application ID.
     *
     * @param id the exercise's application ID; must not be null
     * @return an {@link Optional} containing the exercise, or empty if not found
     */
    Optional<Exercise> findById(Long id);

    /**
     * Finds an exercise by its unique name.
     *
     * @param name the exercise name; must not be null or blank
     * @return an {@link Optional} containing the exercise, or empty if not found
     */
    Optional<Exercise> findByName(String name);

    /**
     * Deletes the exercise with the given ID. If no such exercise exists, this is a no-op.
     *
     * @param id the ID of the exercise to delete; must not be null
     */
    void delete(Long id);
}
