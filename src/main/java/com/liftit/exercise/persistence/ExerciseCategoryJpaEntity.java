package com.liftit.exercise.persistence;

import com.liftit.exercise.ExerciseCategory;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;

/**
 * JPA entity mapping to the {@code exercise_categories} table.
 *
 * <p>Acts as the persistence layer representation of an {@link ExerciseCategory} domain record.
 * Conversion to/from the domain model is provided by {@link #toDomain()} and
 * {@link #fromDomain(ExerciseCategory)}.
 *
 * <p>Note: {@code id} has no {@code @GeneratedValue} — category IDs are hardcoded
 * via seed data and are never DB-generated.
 */
@Entity
@Table(name = "exercise_categories")
class ExerciseCategoryJpaEntity {

    @Id
    private Long id;

    @Column(name = "name", nullable = false, unique = true, length = 50)
    private String name;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "created_by", nullable = false)
    private Long createdBy;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @Column(name = "updated_by", nullable = false)
    private Long updatedBy;

    /** Required by JPA. */
    protected ExerciseCategoryJpaEntity() {
    }

    private ExerciseCategoryJpaEntity(
            Long id,
            String name,
            Instant createdAt,
            Long createdBy,
            Instant updatedAt,
            Long updatedBy) {
        this.id = id;
        this.name = name;
        this.createdAt = createdAt;
        this.createdBy = createdBy;
        this.updatedAt = updatedAt;
        this.updatedBy = updatedBy;
    }

    /**
     * Converts this JPA entity to its corresponding {@link ExerciseCategory} domain record.
     *
     * @return a fully populated {@link ExerciseCategory}
     */
    ExerciseCategory toDomain() {
        return new ExerciseCategory(id, name, createdAt, createdBy, updatedAt, updatedBy);
    }

    /**
     * Creates an {@code ExerciseCategoryJpaEntity} from an {@link ExerciseCategory} domain record.
     *
     * @param category the domain record to convert; must not be null
     * @return a new {@code ExerciseCategoryJpaEntity} ready for persistence
     */
    static ExerciseCategoryJpaEntity fromDomain(ExerciseCategory category) {
        return new ExerciseCategoryJpaEntity(
                category.id(), category.name(),
                category.createdAt(), category.createdBy(),
                category.updatedAt(), category.updatedBy());
    }
}
