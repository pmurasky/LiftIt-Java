package com.liftit.user.persistence;

import com.liftit.user.UserProfile;
import com.liftit.user.UserProfileRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * JPA-backed implementation of {@link UserProfileRepository}.
 *
 * <p>Adapts the Spring Data {@link UserProfileJpaRepository} to the domain-facing
 * {@link UserProfileRepository} contract. All persistence operations delegate to
 * the Spring Data repository and convert between {@link UserProfileJpaEntity} and
 * the {@link UserProfile} domain record.
 *
 * <p>This class is the only consumer of {@link UserProfileJpaRepository}; all other
 * application code depends on {@link UserProfileRepository} (DIP).
 */
@Repository
class JpaUserProfileRepository implements UserProfileRepository {

    private final UserProfileJpaRepository springDataRepository;

    JpaUserProfileRepository(UserProfileJpaRepository springDataRepository) {
        this.springDataRepository = springDataRepository;
    }

    @Override
    public UserProfile save(UserProfile profile) {
        UserProfileJpaEntity entity = UserProfileJpaEntity.fromDomain(profile);
        return springDataRepository.save(entity).toDomain();
    }

    @Override
    public Optional<UserProfile> findByUserId(Long userId) {
        return springDataRepository.findByUserId(userId).map(UserProfileJpaEntity::toDomain);
    }

    @Override
    public Optional<UserProfile> findByUsername(String username) {
        return springDataRepository.findByUsername(username).map(UserProfileJpaEntity::toDomain);
    }

    @Override
    public void delete(Long id) {
        springDataRepository.deleteById(id);
    }
}
