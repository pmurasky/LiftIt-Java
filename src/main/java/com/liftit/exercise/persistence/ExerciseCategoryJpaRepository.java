package com.liftit.exercise.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Spring Data repository for {@link ExerciseCategoryJpaEntity}.
 *
 * <p>Package-private — never used directly outside this package.
 * All application code depends on {@link com.liftit.exercise.ExerciseCategoryRepository} (DIP).
 */
interface ExerciseCategoryJpaRepository extends JpaRepository<ExerciseCategoryJpaEntity, Long> {
}
