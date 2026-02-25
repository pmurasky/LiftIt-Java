package com.liftit.exercise.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

/**
 * Spring Data repository for {@link ExerciseJpaEntity}.
 *
 * <p>Package-private — never used directly outside this package.
 * All application code depends on {@link com.liftit.exercise.ExerciseRepository} (DIP).
 *
 * <p>Extends {@link JpaSpecificationExecutor} to support dynamic filtering via
 * JPA Criteria API Specifications.
 */
interface ExerciseJpaRepository
        extends JpaRepository<ExerciseJpaEntity, Long>,
                JpaSpecificationExecutor<ExerciseJpaEntity> {

    Optional<ExerciseJpaEntity> findByName(String name);
}
