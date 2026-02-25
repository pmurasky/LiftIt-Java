package com.liftit.muscle.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Spring Data repository for {@link MuscleJpaEntity}.
 *
 * <p>Package-private — never used directly outside this package.
 * All application code depends on {@link com.liftit.muscle.MuscleRepository} (DIP).
 */
interface MuscleJpaRepository extends JpaRepository<MuscleJpaEntity, Long> {
}
