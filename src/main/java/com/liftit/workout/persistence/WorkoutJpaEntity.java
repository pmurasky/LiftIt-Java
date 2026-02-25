package com.liftit.workout.persistence;

import com.liftit.workout.Workout;
import com.liftit.workout.WorkoutExercise;
import com.liftit.workout.WorkoutStatus;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * JPA entity mapping to the {@code workouts} table.
 *
 * <p>Aggregate root entity. Owns a collection of {@link WorkoutExerciseJpaEntity}
 * instances with cascade-all semantics. Converts to/from the {@link Workout}
 * domain aggregate via {@link #toDomain()} and {@link #fromDomain(Workout)}.
 *
 * <p>When {@code workout.id()} is {@code 0}, the {@code id} field is set to
 * {@code null} so the database identity column assigns the real PK.
 */
@Entity
@Table(name = "workouts")
class WorkoutJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "status", nullable = false, length = 20)
    private String status;

    @Column(name = "started_at", nullable = false)
    private Instant startedAt;

    @Column(name = "completed_at")
    private Instant completedAt;

    @Column(name = "notes", length = 1000)
    private String notes;

    @OneToMany(mappedBy = "workout", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @OrderBy("orderIndex ASC")
    private List<WorkoutExerciseJpaEntity> exercises = new ArrayList<>();

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "created_by", nullable = false)
    private Long createdBy;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @Column(name = "updated_by", nullable = false)
    private Long updatedBy;

    /** Required by JPA. */
    protected WorkoutJpaEntity() {
    }

    private WorkoutJpaEntity(
            Long id,
            Long userId,
            String status,
            Instant startedAt,
            Instant completedAt,
            String notes,
            Instant createdAt,
            Long createdBy,
            Instant updatedAt,
            Long updatedBy) {
        this.id = id;
        this.userId = userId;
        this.status = status;
        this.startedAt = startedAt;
        this.completedAt = completedAt;
        this.notes = notes;
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
     * Converts this JPA entity to its corresponding {@link Workout} domain aggregate.
     *
     * @return the domain aggregate
     */
    Workout toDomain() {
        List<WorkoutExercise> domainExercises = exercises.stream()
                .map(WorkoutExerciseJpaEntity::toDomain)
                .toList();
        Long entityId = id == null ? 0L : id;
        return new Workout(entityId, userId, startedAt, completedAt,
                WorkoutStatus.valueOf(status), notes, domainExercises,
                createdAt, createdBy, updatedAt, updatedBy);
    }

    /**
     * Creates a {@code WorkoutJpaEntity} from a {@link Workout} domain aggregate.
     *
     * <p>When {@code workout.id()} is {@code 0}, the {@code id} field is set to
     * {@code null} so the database identity column assigns the real PK.
     *
     * @param workout the domain aggregate; must not be null
     * @return a new entity with its exercises and sets populated, ready for persistence
     */
    static WorkoutJpaEntity fromDomain(Workout workout) {
        Long entityId = workout.id() == 0 ? null : workout.id();
        WorkoutJpaEntity entity = new WorkoutJpaEntity(
                entityId,
                workout.userId(),
                workout.status().name(),
                workout.startedAt(),
                workout.completedAt(),
                workout.notes(),
                workout.createdAt(),
                workout.createdBy(),
                workout.updatedAt(),
                workout.updatedBy());
        workout.exercises().forEach(we -> entity.exercises.add(WorkoutExerciseJpaEntity.fromDomain(we, entity)));
        return entity;
    }
}
