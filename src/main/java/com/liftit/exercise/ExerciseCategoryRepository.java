package com.liftit.exercise;

import java.util.List;

/**
 * Data-access contract for {@link ExerciseCategory} reference data.
 *
 * <p>Exercise categories are system-defined and read-only at runtime. This interface
 * exposes only the operations needed by the application — currently just
 * {@link #findAll()} for startup validation.
 *
 * <p>Callers depend on this abstraction rather than any concrete implementation
 * (Dependency Inversion Principle).
 */
public interface ExerciseCategoryRepository {

    /**
     * Returns all exercise category records from the reference table.
     *
     * @return an unmodifiable list of all exercise categories; never null
     */
    List<ExerciseCategory> findAll();
}
