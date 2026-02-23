package com.liftit.user.persistence;

import com.liftit.user.UserProfile;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

/**
 * JPA entity mapping to the {@code user_profiles} table.
 *
 * <p>Acts as the persistence layer representation of a {@link UserProfile} domain record.
 * Conversion to/from the domain model is provided by {@link #toDomain()} and
 * {@link #fromDomain(UserProfile)}.
 */
@Entity
@Table(name = "user_profiles")
class UserProfileJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false, unique = true)
    private Long userId;

    @Column(name = "username", nullable = false, unique = true, length = 30)
    private String username;

    @Column(name = "display_name", length = 100)
    private String displayName;

    @Column(name = "gender", length = 20)
    private String gender;

    @Column(name = "birthdate")
    private LocalDate birthdate;

    @Column(name = "height_in", precision = 5, scale = 1)
    private BigDecimal heightIn;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "created_by", nullable = false)
    private Long createdBy;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @Column(name = "updated_by", nullable = false)
    private Long updatedBy;

    /** Required by JPA. */
    protected UserProfileJpaEntity() {
    }

    /** Returns the database-assigned PK, or {@code null} for new (unsaved) entities. */
    Long getId() {
        return id;
    }

    private UserProfileJpaEntity(
            Long id,
            Long userId,
            String username,
            String displayName,
            String gender,
            LocalDate birthdate,
            Double heightIn,
            Instant createdAt,
            Long createdBy,
            Instant updatedAt,
            Long updatedBy) {
        this.id = id;
        this.userId = userId;
        this.username = username;
        this.displayName = displayName;
        this.gender = gender;
        this.birthdate = birthdate;
        this.heightIn = heightIn == null ? null : BigDecimal.valueOf(heightIn);
        this.createdAt = createdAt;
        this.createdBy = createdBy;
        this.updatedAt = updatedAt;
        this.updatedBy = updatedBy;
    }

    /**
     * Converts this JPA entity to its corresponding {@link UserProfile} domain record.
     *
     * @return a fully populated {@link UserProfile}
     */
    UserProfile toDomain() {
        Double heightInDouble = heightIn == null ? null : heightIn.doubleValue();
        return new UserProfile(id, userId, username, displayName, gender, birthdate,
                heightInDouble, createdAt, createdBy, updatedAt, updatedBy);
    }

    /**
     * Creates a {@code UserProfileJpaEntity} from a {@link UserProfile} domain record.
     *
     * <p>When {@code profile.id()} is {@code 0}, the {@code id} field is set to
     * {@code null} so that the database identity column assigns the real PK.
     *
     * @param profile the domain record to convert; must not be null
     * @return a new {@code UserProfileJpaEntity} ready for persistence
     */
    static UserProfileJpaEntity fromDomain(UserProfile profile) {
        Long id = profile.id() == 0 ? null : profile.id();
        return new UserProfileJpaEntity(id, profile.userId(), profile.username(),
                profile.displayName(), profile.gender(), profile.birthdate(),
                profile.heightIn(),
                profile.createdAt(), profile.createdBy(), profile.updatedAt(), profile.updatedBy());
    }
}
