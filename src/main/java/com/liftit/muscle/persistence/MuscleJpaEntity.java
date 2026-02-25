package com.liftit.muscle.persistence;

import com.liftit.muscle.Muscle;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;

/**
 * JPA entity mapping to the {@code muscles} table.
 *
 * <p>Acts as the persistence layer representation of a {@link Muscle} domain record.
 * Conversion to/from the domain model is provided by {@link #toDomain()} and
 * {@link #fromDomain(Muscle)}.
 *
 * <p>Note: {@code id} has no {@code @GeneratedValue} — muscle IDs are hardcoded
 * via seed data and are never DB-generated.
 */
@Entity
@Table(name = "muscles")
class MuscleJpaEntity {

    @Id
    private Long id;

    @Column(name = "name", nullable = false, unique = true, length = 20)
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
    protected MuscleJpaEntity() {
    }

    private MuscleJpaEntity(
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
     * Converts this JPA entity to its corresponding {@link Muscle} domain record.
     *
     * @return a fully populated {@link Muscle}
     */
    Muscle toDomain() {
        return new Muscle(id, name, createdAt, createdBy, updatedAt, updatedBy);
    }

    /**
     * Creates a {@code MuscleJpaEntity} from a {@link Muscle} domain record.
     *
     * @param muscle the domain record to convert; must not be null
     * @return a new {@code MuscleJpaEntity} ready for persistence
     */
    static MuscleJpaEntity fromDomain(Muscle muscle) {
        return new MuscleJpaEntity(
                muscle.id(), muscle.name(),
                muscle.createdAt(), muscle.createdBy(),
                muscle.updatedAt(), muscle.updatedBy());
    }
}
