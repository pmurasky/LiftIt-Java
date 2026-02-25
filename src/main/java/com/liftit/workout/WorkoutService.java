package com.liftit.workout;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Application service for workout management.
 *
 * <p>Callers depend on this abstraction (Dependency Inversion Principle).
 * Implementations enforce business rules such as ownership and lifecycle state.
 */
public interface WorkoutService {

    /**
     * Starts a new workout for the given user.
     *
     * @param userId the ID of the authenticated user; must not be null
     * @param notes  optional notes for the workout; may be null
     * @return the persisted workout with its database-assigned ID
     */
    Workout start(Long userId, String notes);

    /**
     * Returns a single workout by ID.
     *
     * @param id the workout ID; must not be null
     * @return the workout
     * @throws com.liftit.workout.exception.WorkoutNotFoundException if not found
     */
    Workout getById(Long id);

    /**
     * Returns a paginated list of workouts belonging to the given user.
     *
     * @param userId   the owning user's ID; must not be null
     * @param pageable pagination and sorting parameters; must not be null
     * @return a page of workouts for the user
     */
    Page<Workout> listByUser(Long userId, Pageable pageable);

    /**
     * Adds an exercise to an in-progress workout.
     *
     * @param workoutId  the ID of the workout to update; must not be null
     * @param exercise   the exercise to add; must not be null
     * @param userId     the ID of the authenticated user; must not be null
     * @return the updated workout
     * @throws com.liftit.workout.exception.WorkoutNotFoundException       if not found
     * @throws com.liftit.workout.exception.WorkoutOwnershipException      if user does not own the workout
     * @throws com.liftit.workout.exception.WorkoutAlreadyCompletedException if the workout is already completed
     */
    Workout addExercise(Long workoutId, WorkoutExercise exercise, Long userId);

    /**
     * Marks a workout as completed.
     *
     * @param workoutId the ID of the workout to complete; must not be null
     * @param userId    the ID of the authenticated user; must not be null
     * @return the completed workout
     * @throws com.liftit.workout.exception.WorkoutNotFoundException       if not found
     * @throws com.liftit.workout.exception.WorkoutOwnershipException      if user does not own the workout
     * @throws com.liftit.workout.exception.WorkoutAlreadyCompletedException if the workout is already completed
     */
    Workout complete(Long workoutId, Long userId);

    /**
     * Deletes a workout. Only the owner may delete.
     *
     * @param workoutId the ID of the workout to delete; must not be null
     * @param userId    the ID of the authenticated user; must not be null
     * @throws com.liftit.workout.exception.WorkoutNotFoundException  if not found
     * @throws com.liftit.workout.exception.WorkoutOwnershipException if user does not own the workout
     */
    void delete(Long workoutId, Long userId);
}
