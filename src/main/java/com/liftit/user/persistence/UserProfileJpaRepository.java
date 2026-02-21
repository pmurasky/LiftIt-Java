package com.liftit.user.persistence;

import com.liftit.user.UserProfileRepository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Spring Data JPA repository for {@link UserProfileJpaEntity}.
 *
 * <p>Provides low-level CRUD operations against the {@code user_profiles} table.
 * Application code must NOT use this interface directly — it is an infrastructure
 * detail consumed only by {@link JpaUserProfileRepository}, which adapts it to the
 * domain-facing {@link UserProfileRepository} contract.
 */
interface UserProfileJpaRepository extends JpaRepository<UserProfileJpaEntity, Long> {

    /**
     * Finds a profile by the owning user's application ID.
     *
     * @param userId the owning user's application ID; must not be null
     * @return an {@link Optional} containing the entity, or empty if not found
     */
    Optional<UserProfileJpaEntity> findByUserId(Long userId);

    /**
     * Finds a profile by the chosen username.
     *
     * @param username the username string; must not be null or blank
     * @return an {@link Optional} containing the entity, or empty if not found
     */
    Optional<UserProfileJpaEntity> findByUsername(String username);
}
