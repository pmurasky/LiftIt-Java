package com.liftit.user;

import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * JPA-backed implementation of {@link UserRepository}.
 *
 * <p>Adapts the Spring Data {@link UserJpaRepository} to the domain-facing
 * {@link UserRepository} contract. All persistence operations delegate to
 * the Spring Data repository and convert between {@link UserJpaEntity} and
 * the {@link User} domain record.
 *
 * <p>This class is the only consumer of {@link UserJpaRepository}; all other
 * application code depends on {@link UserRepository} (DIP).
 */
@Repository
class JpaUserRepository implements UserRepository {

    private final UserJpaRepository springDataRepository;

    JpaUserRepository(UserJpaRepository springDataRepository) {
        this.springDataRepository = springDataRepository;
    }

    @Override
    public User save(User user) {
        UserJpaEntity entity = UserJpaEntity.fromDomain(user);
        return springDataRepository.save(entity).toDomain();
    }

    @Override
    public Optional<User> findById(Long id) {
        return springDataRepository.findById(id).map(UserJpaEntity::toDomain);
    }

    @Override
    public Optional<User> findByAuth0Id(Auth0Id auth0Id) {
        return springDataRepository.findByAuth0Id(auth0Id.value()).map(UserJpaEntity::toDomain);
    }

    @Override
    public Optional<User> findByEmail(Email email) {
        return springDataRepository.findByEmail(email.value()).map(UserJpaEntity::toDomain);
    }

    @Override
    public void delete(Long id) {
        springDataRepository.deleteById(id);
    }
}
