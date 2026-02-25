package com.liftit.workout.persistence;

import com.liftit.workout.Workout;
import com.liftit.workout.WorkoutRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * JPA-backed implementation of {@link WorkoutRepository}.
 *
 * <p>Adapts the Spring Data {@link WorkoutJpaRepository} to the domain-facing
 * {@link WorkoutRepository} contract. All persistence operations delegate to
 * the Spring Data repository and convert between {@link WorkoutJpaEntity} and
 * the {@link Workout} domain aggregate.
 *
 * <p>This class is the only consumer of {@link WorkoutJpaRepository}; all other
 * application code depends on {@link WorkoutRepository} (DIP).
 */
@Repository
class JpaWorkoutRepository implements WorkoutRepository {

    private final WorkoutJpaRepository springDataRepository;

    JpaWorkoutRepository(WorkoutJpaRepository springDataRepository) {
        this.springDataRepository = springDataRepository;
    }

    @Override
    public Workout save(Workout workout) {
        return springDataRepository.save(WorkoutJpaEntity.fromDomain(workout)).toDomain();
    }

    @Override
    public Optional<Workout> findById(Long id) {
        return springDataRepository.findById(id).map(WorkoutJpaEntity::toDomain);
    }

    @Override
    public Page<Workout> findByUserId(Long userId, Pageable pageable) {
        return springDataRepository.findByUserId(userId, pageable).map(WorkoutJpaEntity::toDomain);
    }

    @Override
    public void delete(Long id) {
        springDataRepository.deleteById(id);
    }
}
