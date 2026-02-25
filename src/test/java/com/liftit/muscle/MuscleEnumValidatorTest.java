package com.liftit.muscle;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.ApplicationArguments;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MuscleEnumValidatorTest {

    private static final Instant NOW = Instant.parse("2026-01-01T00:00:00Z");
    private static final Long SYSTEM_USER_ID = 1L;

    @Mock
    private MuscleRepository muscleRepository;

    private MuscleEnumValidator validator;

    @BeforeEach
    void setUp() {
        validator = new MuscleEnumValidator(muscleRepository);
    }

    @Test
    void shouldPassWhenEnumAndTableAreInSync() {
        // Given — all 10 muscles match the 10 MuscleEnum values
        when(muscleRepository.findAll()).thenReturn(allTenMuscles());

        // When / Then
        assertDoesNotThrow(() -> validator.run(mock(ApplicationArguments.class)));
    }

    @Test
    void shouldThrowWhenTableHasFewerRowsThanEnum() {
        // Given — only 9 muscles in the DB
        when(muscleRepository.findAll()).thenReturn(allTenMuscles().subList(0, 9));

        // When / Then
        assertThrows(IllegalStateException.class,
                () -> validator.run(mock(ApplicationArguments.class)));
    }

    @Test
    void shouldThrowWhenTableHasMoreRowsThanEnum() {
        // Given — 11 muscles in the DB (extra row with id=11)
        List<Muscle> eleven = new java.util.ArrayList<>(allTenMuscles());
        eleven.add(new Muscle(11L, "Glutes", NOW, SYSTEM_USER_ID, NOW, SYSTEM_USER_ID));
        when(muscleRepository.findAll()).thenReturn(eleven);

        // When / Then
        assertThrows(IllegalStateException.class,
                () -> validator.run(mock(ApplicationArguments.class)));
    }

    @Test
    void shouldThrowWhenTableHasWrongIds() {
        // Given — correct count but wrong IDs (id=99 instead of id=10)
        List<Muscle> wrongIds = new java.util.ArrayList<>(allTenMuscles().subList(0, 9));
        wrongIds.add(new Muscle(99L, "Unknown", NOW, SYSTEM_USER_ID, NOW, SYSTEM_USER_ID));
        when(muscleRepository.findAll()).thenReturn(wrongIds);

        // When / Then
        assertThrows(IllegalStateException.class,
                () -> validator.run(mock(ApplicationArguments.class)));
    }

    @Test
    void shouldThrowWhenTableIsEmpty() {
        // Given
        when(muscleRepository.findAll()).thenReturn(List.of());

        // When / Then
        assertThrows(IllegalStateException.class,
                () -> validator.run(mock(ApplicationArguments.class)));
    }

    private List<Muscle> allTenMuscles() {
        return List.of(
                new Muscle(1L,  "Abdominals", NOW, SYSTEM_USER_ID, NOW, SYSTEM_USER_ID),
                new Muscle(2L,  "Back",       NOW, SYSTEM_USER_ID, NOW, SYSTEM_USER_ID),
                new Muscle(3L,  "Biceps",     NOW, SYSTEM_USER_ID, NOW, SYSTEM_USER_ID),
                new Muscle(4L,  "Calves",     NOW, SYSTEM_USER_ID, NOW, SYSTEM_USER_ID),
                new Muscle(5L,  "Chest",      NOW, SYSTEM_USER_ID, NOW, SYSTEM_USER_ID),
                new Muscle(6L,  "Forearms",   NOW, SYSTEM_USER_ID, NOW, SYSTEM_USER_ID),
                new Muscle(7L,  "Neck",       NOW, SYSTEM_USER_ID, NOW, SYSTEM_USER_ID),
                new Muscle(8L,  "Shoulders",  NOW, SYSTEM_USER_ID, NOW, SYSTEM_USER_ID),
                new Muscle(9L,  "Thighs",     NOW, SYSTEM_USER_ID, NOW, SYSTEM_USER_ID),
                new Muscle(10L, "Triceps",    NOW, SYSTEM_USER_ID, NOW, SYSTEM_USER_ID)
        );
    }
}
