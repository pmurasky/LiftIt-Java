package com.liftit.exercise.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Spring Data repository for {@link ExerciseJpaEntity}.
 *
 * <p>Package-private — never used directly outside this package.
 * All application code depends on {@link com.liftit.exercise.ExerciseRepository} (DIP).
 */
interface ExerciseJpaRepository extends JpaRepository<ExerciseJpaEntity, Long> {

    Optional<ExerciseJpaEntity> findByName(String name);
}
