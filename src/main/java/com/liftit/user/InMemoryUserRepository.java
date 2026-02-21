package com.liftit.user;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/// Thread-safe in-memory implementation of `UserRepository`.
///
/// Backed by a `ConcurrentHashMap` keyed by `UserId`. Uniqueness constraints
/// on `username` and `email` are enforced eagerly: `save()` throws
/// `IllegalArgumentException` when a conflicting record exists for a
/// *different* user.
///
/// Intended for unit and integration testing only — not for production use.
public class InMemoryUserRepository implements UserRepository {

    private final Map<UserId, User> store = new ConcurrentHashMap<>();

    @Override
    public User save(User user) {
        Objects.requireNonNull(user, "user must not be null");
        assertUsernameNotTaken(user);
        assertEmailNotTaken(user);
        store.put(user.id(), user);
        return user;
    }

    @Override
    public Optional<User> findById(UserId id) {
        Objects.requireNonNull(id, "id must not be null");
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public Optional<User> findByUsername(Username username) {
        Objects.requireNonNull(username, "username must not be null");
        return store.values().stream()
            .filter(u -> u.username().equals(username))
            .findFirst();
    }

    @Override
    public Optional<User> findByEmail(Email email) {
        Objects.requireNonNull(email, "email must not be null");
        return store.values().stream()
            .filter(u -> u.email().equals(email))
            .findFirst();
    }

    @Override
    public void delete(UserId id) {
        Objects.requireNonNull(id, "id must not be null");
        store.remove(id);
    }

    private void assertUsernameNotTaken(User user) {
        store.values().stream()
            .filter(u -> !u.id().equals(user.id()))
            .filter(u -> u.username().equals(user.username()))
            .findFirst()
            .ifPresent(_ -> {
                throw new IllegalArgumentException(
                    "Username already taken: " + user.username().value());
            });
    }

    private void assertEmailNotTaken(User user) {
        store.values().stream()
            .filter(u -> !u.id().equals(user.id()))
            .filter(u -> u.email().equals(user.email()))
            .findFirst()
            .ifPresent(_ -> {
                throw new IllegalArgumentException(
                    "Email already taken: " + user.email().value());
            });
    }
}
