package com.liftit.exercise;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Application service for exercise management.
 *
 * <p>Callers depend on this abstraction (Dependency Inversion Principle).
 * Implementations enforce business rules such as authorization and uniqueness.
 */
public interface ExerciseService {

    /**
     * Creates a new custom exercise owned by the given user.
     *
     * @param request the exercise details; must not be null
     * @param userId  the ID of the authenticated user creating the exercise
     * @return the persisted exercise
     * @throws com.liftit.exercise.exception.DuplicateExerciseException if a name conflict exists
     */
    Exercise create(CreateExerciseRequest request, Long userId);

    /**
     * Returns a single exercise by ID.
     *
     * @param id the exercise ID
     * @return the exercise
     * @throws com.liftit.exercise.exception.ExerciseNotFoundException if not found
     */
    Exercise getById(Long id);

    /**
     * Updates an existing exercise. Only the owner may update.
     *
     * @param id      the ID of the exercise to update
     * @param request the replacement fields; must not be null
     * @param userId  the ID of the authenticated user
     * @return the updated exercise
     * @throws com.liftit.exercise.exception.ExerciseNotFoundException  if not found
     * @throws com.liftit.exercise.exception.ExerciseOwnershipException if the user does not own the exercise
     * @throws com.liftit.exercise.exception.DuplicateExerciseException if the new name conflicts
     */
    Exercise update(Long id, UpdateExerciseRequest request, Long userId);

    /**
     * Deletes an existing exercise. Only the owner may delete.
     *
     * @param id     the ID of the exercise to delete
     * @param userId the ID of the authenticated user
     * @throws com.liftit.exercise.exception.ExerciseNotFoundException  if not found
     * @throws com.liftit.exercise.exception.ExerciseOwnershipException if the user does not own the exercise
     */
    void delete(Long id, Long userId);

    /**
     * Returns a paginated, filtered list of all exercises.
     *
     * @param filter   the filter criteria; must not be null
     * @param pageable the pagination parameters; must not be null
     * @return a page of matching exercises
     */
    Page<Exercise> list(ExerciseFilter filter, Pageable pageable);

    /**
     * Returns all available exercise categories.
     *
     * @return a list of all exercise categories
     */
    List<ExerciseCategory> getCategories();

    /**
     * Returns all available muscle groups.
     *
     * @return a list of all muscle group enum constants
     */
    List<com.liftit.muscle.MuscleEnum> getMuscleGroups();
}
