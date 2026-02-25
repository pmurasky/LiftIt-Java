package com.liftit.muscle.persistence;

import com.liftit.muscle.Muscle;
import com.liftit.muscle.MuscleRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * JPA-backed implementation of {@link MuscleRepository}.
 *
 * <p>Adapts the Spring Data {@link MuscleJpaRepository} to the domain-facing
 * {@link MuscleRepository} contract. All persistence operations delegate to
 * the Spring Data repository and convert between {@link MuscleJpaEntity} and
 * the {@link Muscle} domain record.
 *
 * <p>This class is the only consumer of {@link MuscleJpaRepository}; all other
 * application code depends on {@link MuscleRepository} (DIP).
 */
@Repository
class JpaMuscleRepository implements MuscleRepository {

    private final MuscleJpaRepository springDataRepository;

    JpaMuscleRepository(MuscleJpaRepository springDataRepository) {
        this.springDataRepository = springDataRepository;
    }

    @Override
    public List<Muscle> findAll() {
        return springDataRepository.findAll()
                .stream()
                .map(MuscleJpaEntity::toDomain)
                .toList();
    }
}
