package com.liftit.workout.persistence;

import com.liftit.workout.WorkoutExercise;
import com.liftit.workout.WorkoutSet;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;

import java.util.ArrayList;
import java.util.List;

/**
 * JPA entity mapping to the {@code workout_exercises} table.
 *
 * <p>Owned by {@link WorkoutJpaEntity}. Owns a collection of
 * {@link WorkoutSetJpaEntity} instances. Converts to/from the
 * {@link WorkoutExercise} domain entity via {@link #toDomain()} and
 * {@link #fromDomain(WorkoutExercise, WorkoutJpaEntity)}.
 *
 * <p>When {@code workoutExercise.id()} is {@code 0}, the {@code id} field
 * is set to {@code null} so the database identity column assigns the real PK.
 */
@Entity
@Table(name = "workout_exercises")
class WorkoutExerciseJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "workout_id", nullable = false)
    private WorkoutJpaEntity workout;

    @Column(name = "exercise_id", nullable = false)
    private Long exerciseId;

    @Column(name = "order_index", nullable = false)
    private Integer orderIndex;

    @Column(name = "notes", length = 500)
    private String notes;

    @OneToMany(mappedBy = "workoutExercise", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @OrderBy("setNumber ASC")
    private List<WorkoutSetJpaEntity> sets = new ArrayList<>();

    /** Required by JPA. */
    protected WorkoutExerciseJpaEntity() {
    }

    private WorkoutExerciseJpaEntity(
            Long id,
            WorkoutJpaEntity workout,
            Long exerciseId,
            Integer orderIndex,
            String notes) {
        this.id = id;
        this.workout = workout;
        this.exerciseId = exerciseId;
        this.orderIndex = orderIndex;
        this.notes = notes;
    }

    /** Returns the database-assigned PK, or {@code null} for new (unsaved) entities. */
    Long getId() {
        return id;
    }

    /**
     * Converts this JPA entity to its corresponding {@link WorkoutExercise} domain entity.
     *
     * @return the domain entity
     */
    WorkoutExercise toDomain() {
        List<WorkoutSet> domainSets = sets.stream()
                .map(WorkoutSetJpaEntity::toDomain)
                .toList();
        Long entityId = id == null ? 0L : id;
        return new WorkoutExercise(entityId, exerciseId, orderIndex, domainSets, notes);
    }

    /**
     * Creates a {@code WorkoutExerciseJpaEntity} from a {@link WorkoutExercise} domain entity.
     *
     * <p>When {@code we.id()} is {@code 0}, the {@code id} field is set to {@code null}
     * so the database identity column assigns the real PK.
     *
     * @param we      the domain entity; must not be null
     * @param workout the owning workout entity; must not be null
     * @return a new entity with its sets populated, ready for persistence
     */
    static WorkoutExerciseJpaEntity fromDomain(WorkoutExercise we, WorkoutJpaEntity workout) {
        Long entityId = we.id() == 0 ? null : we.id();
        WorkoutExerciseJpaEntity entity =
                new WorkoutExerciseJpaEntity(entityId, workout, we.exerciseId(), we.order(), we.notes());
        we.sets().forEach(set -> entity.sets.add(WorkoutSetJpaEntity.fromDomain(set, entity)));
        return entity;
    }
}
