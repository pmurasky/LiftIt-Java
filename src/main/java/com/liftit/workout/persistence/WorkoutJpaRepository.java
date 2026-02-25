package com.liftit.workout.persistence;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Spring Data repository for {@link WorkoutJpaEntity}.
 *
 * <p>Package-private — never used directly outside this package.
 * All application code depends on {@link com.liftit.workout.WorkoutRepository} (DIP).
 */
interface WorkoutJpaRepository extends JpaRepository<WorkoutJpaEntity, Long> {

    Page<WorkoutJpaEntity> findByUserId(Long userId, Pageable pageable);
}
