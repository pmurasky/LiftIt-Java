package com.liftit.user.persistence;

import com.liftit.user.UserRepository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Spring Data JPA repository for {@link UserJpaEntity}.
 *
 * <p>Provides the low-level CRUD operations against the {@code users} table.
 * Application code must NOT use this interface directly — it is an infrastructure
 * detail consumed only by {@link JpaUserRepository}, which adapts it to the
 * domain-facing {@link UserRepository} contract.
 */
interface UserJpaRepository extends JpaRepository<UserJpaEntity, Long> {

    /**
     * Finds a user by their Auth0 subject identifier.
     *
     * @param auth0Id the Auth0 {@code sub} claim string; must not be null
     * @return an {@link Optional} containing the entity, or empty if not found
     */
    Optional<UserJpaEntity> findByAuth0Id(String auth0Id);

    /**
     * Finds a user by their email address.
     *
     * @param email the normalised (lowercase) email string; must not be null
     * @return an {@link Optional} containing the entity, or empty if not found
     */
    Optional<UserJpaEntity> findByEmail(String email);
}
