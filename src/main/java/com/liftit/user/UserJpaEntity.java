package com.liftit.user;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;

/**
 * JPA entity mapping to the {@code users} table.
 *
 * <p>Acts as the persistence layer representation of a {@link User} domain record.
 * Conversion to/from the domain model is provided by {@link #toDomain()} and
 * {@link #fromDomain(User)}.
 *
 * <p>This class is intentionally kept in the {@code user} package alongside the
 * domain model — it is an infrastructure detail of that domain, not a separate layer.
 */
@Entity
@Table(name = "users")
class UserJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "auth0_id", nullable = false, unique = true)
    private String auth0Id;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "created_by", nullable = false)
    private Long createdBy;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @Column(name = "updated_by", nullable = false)
    private Long updatedBy;

    /** Required by JPA. */
    protected UserJpaEntity() {
    }

    /** Returns the database-assigned PK, or {@code null} for new (unsaved) entities. */
    Long getId() {
        return id;
    }

    private UserJpaEntity(
            Long id,
            String auth0Id,
            String email,
            Instant createdAt,
            Long createdBy,
            Instant updatedAt,
            Long updatedBy) {
        this.id = id;
        this.auth0Id = auth0Id;
        this.email = email;
        this.createdAt = createdAt;
        this.createdBy = createdBy;
        this.updatedAt = updatedAt;
        this.updatedBy = updatedBy;
    }

    /**
     * Converts this JPA entity to its corresponding {@link User} domain record.
     *
     * @return a fully populated {@link User}
     */
    User toDomain() {
        return new User(id, Auth0Id.of(auth0Id), Email.of(email),
                createdAt, createdBy, updatedAt, updatedBy);
    }

    /**
     * Creates a {@code UserJpaEntity} from a {@link User} domain record.
     *
     * <p>When {@code user.id()} is {@code 0}, the {@code id} field is set to
     * {@code null} so that the database identity column assigns the real PK.
     *
     * @param user the domain record to convert; must not be null
     * @return a new {@code UserJpaEntity} ready for persistence
     */
    static UserJpaEntity fromDomain(User user) {
        Long id = user.id() == 0 ? null : user.id();
        return new UserJpaEntity(id, user.auth0Id().value(), user.email().value(),
                user.createdAt(), user.createdBy(), user.updatedAt(), user.updatedBy());
    }
}
