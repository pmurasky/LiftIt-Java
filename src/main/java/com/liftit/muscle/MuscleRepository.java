package com.liftit.muscle;

import java.util.List;

/**
 * Data-access contract for {@link Muscle} reference data.
 *
 * <p>Muscles are system-defined and read-only at runtime. This interface
 * exposes only the operations needed by the application — currently just
 * {@link #findAll()} for startup validation.
 *
 * <p>Callers depend on this abstraction rather than any concrete implementation
 * (Dependency Inversion Principle).
 */
public interface MuscleRepository {

    /**
     * Returns all muscle records from the reference table.
     *
     * @return an unmodifiable list of all muscles; never null
     */
    List<Muscle> findAll();
}
