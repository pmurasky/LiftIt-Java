package com.liftit.exercise.persistence;

import com.liftit.exercise.ExerciseCategory;
import com.liftit.exercise.ExerciseCategoryRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * JPA-backed implementation of {@link ExerciseCategoryRepository}.
 *
 * <p>Adapts the Spring Data {@link ExerciseCategoryJpaRepository} to the domain-facing
 * {@link ExerciseCategoryRepository} contract. All persistence operations delegate to
 * the Spring Data repository and convert between {@link ExerciseCategoryJpaEntity} and
 * the {@link ExerciseCategory} domain record.
 *
 * <p>This class is the only consumer of {@link ExerciseCategoryJpaRepository}; all other
 * application code depends on {@link ExerciseCategoryRepository} (DIP).
 */
@Repository
class JpaExerciseCategoryRepository implements ExerciseCategoryRepository {

    private final ExerciseCategoryJpaRepository springDataRepository;

    JpaExerciseCategoryRepository(ExerciseCategoryJpaRepository springDataRepository) {
        this.springDataRepository = springDataRepository;
    }

    @Override
    public List<ExerciseCategory> findAll() {
        return springDataRepository.findAll()
                .stream()
                .map(ExerciseCategoryJpaEntity::toDomain)
                .toList();
    }
}
