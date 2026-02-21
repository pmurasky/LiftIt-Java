package com.liftit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryUserRepositoryTest {

    private UserRepository repository;
    private User alice;
    private User bob;

    @BeforeEach
    void setUp() {
        repository = new InMemoryUserRepository();
        alice = User.create(new Username("alice"), new Email("alice@example.com"));
        bob = User.create(new Username("bob"), new Email("bob@example.com"));
    }

    // ── save ──────────────────────────────────────────────────────────────────

    @Nested
    class WhenSaving {

        @Test
        void shouldReturnSavedUser() {
            // When
            User saved = repository.save(alice);

            // Then
            assertEquals(alice, saved);
        }

        @Test
        void shouldAllowUpdatingSameUser() {
            // Given
            repository.save(alice);
            User updated = new User(alice.id(), new Username("alice2"),
                new Email("alice2@example.com"), alice.createdAt());

            // When / Then — no exception expected
            assertDoesNotThrow(() -> repository.save(updated));
        }

        @Test
        void shouldThrowWhenUsernameTakenByDifferentUser() {
            // Given
            repository.save(alice);
            User clash = User.create(alice.username(), new Email("other@example.com"));

            // When / Then
            assertThrows(IllegalArgumentException.class, () -> repository.save(clash));
        }

        @Test
        void shouldThrowWhenEmailTakenByDifferentUser() {
            // Given
            repository.save(alice);
            User clash = User.create(new Username("other"), alice.email());

            // When / Then
            assertThrows(IllegalArgumentException.class, () -> repository.save(clash));
        }

        @Test
        void shouldThrowWhenUserIsNull() {
            // Given / When / Then
            assertThrows(NullPointerException.class, () -> repository.save(null));
        }
    }

    // ── findById ──────────────────────────────────────────────────────────────

    @Nested
    class WhenFindingById {

        @Test
        void shouldReturnUserWhenFound() {
            // Given
            repository.save(alice);

            // When
            Optional<User> result = repository.findById(alice.id());

            // Then
            assertTrue(result.isPresent());
            assertEquals(alice, result.get());
        }

        @Test
        void shouldReturnEmptyWhenNotFound() {
            // When
            Optional<User> result = repository.findById(UserId.generate());

            // Then
            assertTrue(result.isEmpty());
        }

        @Test
        void shouldThrowWhenIdIsNull() {
            // Given / When / Then
            assertThrows(NullPointerException.class, () -> repository.findById(null));
        }
    }

    // ── findByUsername ────────────────────────────────────────────────────────

    @Nested
    class WhenFindingByUsername {

        @Test
        void shouldReturnUserWhenFound() {
            // Given
            repository.save(alice);

            // When
            Optional<User> result = repository.findByUsername(alice.username());

            // Then
            assertTrue(result.isPresent());
            assertEquals(alice, result.get());
        }

        @Test
        void shouldReturnEmptyWhenNotFound() {
            // When
            Optional<User> result = repository.findByUsername(new Username("ghost"));

            // Then
            assertTrue(result.isEmpty());
        }

        @Test
        void shouldThrowWhenUsernameIsNull() {
            // Given / When / Then
            assertThrows(NullPointerException.class, () -> repository.findByUsername(null));
        }
    }

    // ── findByEmail ───────────────────────────────────────────────────────────

    @Nested
    class WhenFindingByEmail {

        @Test
        void shouldReturnUserWhenFound() {
            // Given
            repository.save(alice);

            // When
            Optional<User> result = repository.findByEmail(alice.email());

            // Then
            assertTrue(result.isPresent());
            assertEquals(alice, result.get());
        }

        @Test
        void shouldReturnEmptyWhenNotFound() {
            // When
            Optional<User> result = repository.findByEmail(new Email("ghost@example.com"));

            // Then
            assertTrue(result.isEmpty());
        }

        @Test
        void shouldThrowWhenEmailIsNull() {
            // Given / When / Then
            assertThrows(NullPointerException.class, () -> repository.findByEmail(null));
        }
    }

    // ── delete ────────────────────────────────────────────────────────────────

    @Nested
    class WhenDeleting {

        @Test
        void shouldRemoveUserFromStore() {
            // Given
            repository.save(alice);

            // When
            repository.delete(alice.id());

            // Then
            assertTrue(repository.findById(alice.id()).isEmpty());
        }

        @Test
        void shouldBeNoOpWhenUserDoesNotExist() {
            // Given / When / Then — no exception expected
            assertDoesNotThrow(() -> repository.delete(UserId.generate()));
        }

        @Test
        void shouldThrowWhenIdIsNull() {
            // Given / When / Then
            assertThrows(NullPointerException.class, () -> repository.delete(null));
        }

        @Test
        void shouldNotAffectOtherUsersWhenDeleting() {
            // Given
            repository.save(alice);
            repository.save(bob);

            // When
            repository.delete(alice.id());

            // Then
            assertTrue(repository.findById(bob.id()).isPresent());
        }
    }

    // ── uniqueness after delete ───────────────────────────────────────────────

    @Test
    void shouldAllowReusingUsernameAfterDelete() {
        // Given
        repository.save(alice);
        repository.delete(alice.id());
        User reuse = User.create(alice.username(), new Email("new@example.com"));

        // When / Then — no exception expected
        assertDoesNotThrow(() -> repository.save(reuse));
    }

    @Test
    void shouldAllowReusingEmailAfterDelete() {
        // Given
        repository.save(alice);
        repository.delete(alice.id());
        User reuse = User.create(new Username("newuser"), alice.email());

        // When / Then — no exception expected
        assertDoesNotThrow(() -> repository.save(reuse));
    }
}
