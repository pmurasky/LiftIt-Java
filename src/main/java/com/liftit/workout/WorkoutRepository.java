package com.liftit.workout;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

/**
 * Data-access contract for {@link Workout} aggregates.
 *
 * <p>Callers depend on this abstraction rather than any concrete implementation
 * (Dependency Inversion Principle). Implementations may store workouts in a
 * relational database, in-memory store, or any other backing mechanism.
 */
public interface WorkoutRepository {

    /**
     * Persists a workout. If a workout with the same {@code id} already exists,
     * it is replaced (full update).
     *
     * @param workout the workout to save; must not be null
     * @return the saved workout with its database-assigned ID
     */
    Workout save(Workout workout);

    /**
     * Finds a workout by its internal application ID.
     *
     * @param id the workout's application ID; must not be null
     * @return an {@link Optional} containing the workout, or empty if not found
     */
    Optional<Workout> findById(Long id);

    /**
     * Returns a paginated list of workouts belonging to the given user.
     *
     * @param userId   the owning user's ID; must not be null
     * @param pageable pagination and sorting parameters; must not be null
     * @return a page of workouts for the user
     */
    Page<Workout> findByUserId(Long userId, Pageable pageable);

    /**
     * Deletes the workout with the given ID. If no such workout exists, this is a no-op.
     *
     * @param id the ID of the workout to delete; must not be null
     */
    void delete(Long id);
}
