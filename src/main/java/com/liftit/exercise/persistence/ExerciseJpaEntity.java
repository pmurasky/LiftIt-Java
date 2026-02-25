package com.liftit.exercise.persistence;

import com.liftit.exercise.Exercise;
import com.liftit.exercise.ExerciseCategoryEnum;
import com.liftit.muscle.MuscleEnum;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * JPA entity mapping to the {@code exercises} and {@code exercise_muscle_groups} tables.
 *
 * <p>Acts as the persistence layer representation of an {@link Exercise} domain record.
 * Conversion to/from the domain model is provided by {@link #toDomain()} and
 * {@link #fromDomain(Exercise)}.
 *
 * <p>When {@code exercise.id()} is {@code 0}, the {@code id} field is set to {@code null}
 * so that the database identity column assigns the real PK.
 *
 * <p>The {@code category_id} column stores the {@link ExerciseCategoryEnum#getCategoryId()} value.
 * The {@code exercise_muscle_groups} join table stores the {@link MuscleEnum#getMuscleId()} value
 * for each targeted muscle group.
 */
@Entity
@Table(name = "exercises")
class ExerciseJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, unique = true, length = 100)
    private String name;

    @Column(name = "category_id", nullable = false)
    private Long categoryId;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "exercise_muscle_groups", joinColumns = @JoinColumn(name = "exercise_id"))
    @Column(name = "muscle_id", nullable = false)
    private Set<Long> muscleIds;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "created_by", nullable = false)
    private Long createdBy;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @Column(name = "updated_by", nullable = false)
    private Long updatedBy;

    /** Required by JPA. */
    protected ExerciseJpaEntity() {
    }

    private ExerciseJpaEntity(
            Long id,
            String name,
            Long categoryId,
            Set<Long> muscleIds,
            Instant createdAt,
            Long createdBy,
            Instant updatedAt,
            Long updatedBy) {
        this.id = id;
        this.name = name;
        this.categoryId = categoryId;
        this.muscleIds = muscleIds;
        this.createdAt = createdAt;
        this.createdBy = createdBy;
        this.updatedAt = updatedAt;
        this.updatedBy = updatedBy;
    }

    /** Returns the database-assigned PK, or {@code null} for new (unsaved) entities. */
    Long getId() {
        return id;
    }

    /**
     * Converts this JPA entity to its corresponding {@link Exercise} domain record.
     *
     * @return a fully populated {@link Exercise}
     */
    Exercise toDomain() {
        ExerciseCategoryEnum category = ExerciseCategoryEnum.fromCategoryId(categoryId);
        Set<MuscleEnum> muscleGroups = muscleIds.stream()
                .map(MuscleEnum::fromMuscleId)
                .collect(Collectors.toSet());
        return new Exercise(id, name, category, muscleGroups, createdAt, createdBy, updatedAt, updatedBy);
    }

    /**
     * Creates an {@code ExerciseJpaEntity} from an {@link Exercise} domain record.
     *
     * <p>When {@code exercise.id()} is {@code 0}, the {@code id} field is set to
     * {@code null} so that the database identity column assigns the real PK.
     *
     * @param exercise the domain record to convert; must not be null
     * @return a new {@code ExerciseJpaEntity} ready for persistence
     */
    static ExerciseJpaEntity fromDomain(Exercise exercise) {
        Long id = exercise.id() == 0 ? null : exercise.id();
        Long categoryId = exercise.category().getCategoryId();
        Set<Long> muscleIds = exercise.muscleGroups().stream()
                .map(MuscleEnum::getMuscleId)
                .collect(Collectors.toSet());
        return new ExerciseJpaEntity(id, exercise.name(), categoryId, muscleIds,
                exercise.createdAt(), exercise.createdBy(), exercise.updatedAt(), exercise.updatedBy());
    }
}
