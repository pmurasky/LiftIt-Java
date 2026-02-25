package com.liftit.exercise.persistence;

import com.liftit.exercise.Exercise;
import com.liftit.exercise.ExerciseFilter;
import com.liftit.exercise.ExerciseRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * JPA-backed implementation of {@link ExerciseRepository}.
 *
 * <p>Adapts the Spring Data {@link ExerciseJpaRepository} to the domain-facing
 * {@link ExerciseRepository} contract. All persistence operations delegate to
 * the Spring Data repository and convert between {@link ExerciseJpaEntity} and
 * the {@link Exercise} domain record.
 *
 * <p>This class is the only consumer of {@link ExerciseJpaRepository}; all other
 * application code depends on {@link ExerciseRepository} (DIP).
 */
@Repository
class JpaExerciseRepository implements ExerciseRepository {

    private final ExerciseJpaRepository springDataRepository;

    JpaExerciseRepository(ExerciseJpaRepository springDataRepository) {
        this.springDataRepository = springDataRepository;
    }

    @Override
    public Exercise save(Exercise exercise) {
        return springDataRepository.save(ExerciseJpaEntity.fromDomain(exercise)).toDomain();
    }

    @Override
    public Optional<Exercise> findById(Long id) {
        return springDataRepository.findById(id).map(ExerciseJpaEntity::toDomain);
    }

    @Override
    public Optional<Exercise> findByName(String name) {
        return springDataRepository.findByName(name).map(ExerciseJpaEntity::toDomain);
    }

    @Override
    public Page<Exercise> findAll(ExerciseFilter filter, Pageable pageable) {
        return springDataRepository
                .findAll(ExerciseSpecifications.from(filter), pageable)
                .map(ExerciseJpaEntity::toDomain);
    }

    @Override
    public void delete(Long id) {
        springDataRepository.deleteById(id);
    }
}
