package com.liftit.exercise.persistence;

import com.liftit.exercise.ExerciseCategoryEnum;
import com.liftit.exercise.ExerciseFilter;
import com.liftit.muscle.MuscleEnum;
import jakarta.persistence.criteria.Join;
import org.springframework.data.jpa.domain.Specification;

/**
 * JPA Specification factory for {@link ExerciseJpaEntity} queries.
 *
 * <p>Each method returns a composable predicate. The {@link #from(ExerciseFilter)}
 * factory combines all active filters with AND logic. A {@code null} filter field
 * means "no restriction" and is skipped.
 */
class ExerciseSpecifications {

    private ExerciseSpecifications() {
    }

    /**
     * Builds a composite {@link Specification} from the given filter.
     *
     * <p>Only non-null filter fields contribute predicates.
     *
     * @param filter the filter criteria; must not be null
     * @return a specification matching all active filter constraints
     */
    static Specification<ExerciseJpaEntity> from(ExerciseFilter filter) {
        Specification<ExerciseJpaEntity> spec = (root, query, cb) -> null;
        if (filter.category() != null) {
            spec = spec.and(hasCategory(filter.category()));
        }
        if (filter.muscleGroup() != null) {
            spec = spec.and(hasMuscleGroup(filter.muscleGroup()));
        }
        if (filter.search() != null && !filter.search().isBlank()) {
            spec = spec.and(nameContains(filter.search()));
        }
        return spec;
    }

    private static Specification<ExerciseJpaEntity> hasCategory(ExerciseCategoryEnum category) {
        return (root, query, cb) ->
                cb.equal(root.get("categoryId"), category.getCategoryId());
    }

    private static Specification<ExerciseJpaEntity> hasMuscleGroup(MuscleEnum muscleGroup) {
        return (root, query, cb) -> {
            Join<ExerciseJpaEntity, Long> muscleIds = root.join("muscleIds");
            return cb.equal(muscleIds, muscleGroup.getMuscleId());
        };
    }

    private static Specification<ExerciseJpaEntity> nameContains(String search) {
        return (root, query, cb) ->
                cb.like(cb.lower(root.get("name")), "%" + search.toLowerCase() + "%");
    }
}
