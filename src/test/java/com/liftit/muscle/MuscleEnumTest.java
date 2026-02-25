package com.liftit.muscle;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class MuscleEnumTest {

    @Test
    void shouldHaveTenValues() {
        assertEquals(10, MuscleEnum.values().length);
    }

    @Test
    void shouldHaveCorrectMuscleIds() {
        assertEquals(1L, MuscleEnum.ABDOMINALS.getMuscleId());
        assertEquals(2L, MuscleEnum.BACK.getMuscleId());
        assertEquals(3L, MuscleEnum.BICEPS.getMuscleId());
        assertEquals(4L, MuscleEnum.CALVES.getMuscleId());
        assertEquals(5L, MuscleEnum.CHEST.getMuscleId());
        assertEquals(6L, MuscleEnum.FOREARMS.getMuscleId());
        assertEquals(7L, MuscleEnum.NECK.getMuscleId());
        assertEquals(8L, MuscleEnum.SHOULDERS.getMuscleId());
        assertEquals(9L, MuscleEnum.THIGHS.getMuscleId());
        assertEquals(10L, MuscleEnum.TRICEPS.getMuscleId());
    }

    @Test
    void shouldLookUpEnumByMuscleId() {
        assertEquals(MuscleEnum.ABDOMINALS, MuscleEnum.fromMuscleId(1L));
        assertEquals(MuscleEnum.BACK, MuscleEnum.fromMuscleId(2L));
        assertEquals(MuscleEnum.BICEPS, MuscleEnum.fromMuscleId(3L));
        assertEquals(MuscleEnum.CALVES, MuscleEnum.fromMuscleId(4L));
        assertEquals(MuscleEnum.CHEST, MuscleEnum.fromMuscleId(5L));
        assertEquals(MuscleEnum.FOREARMS, MuscleEnum.fromMuscleId(6L));
        assertEquals(MuscleEnum.NECK, MuscleEnum.fromMuscleId(7L));
        assertEquals(MuscleEnum.SHOULDERS, MuscleEnum.fromMuscleId(8L));
        assertEquals(MuscleEnum.THIGHS, MuscleEnum.fromMuscleId(9L));
        assertEquals(MuscleEnum.TRICEPS, MuscleEnum.fromMuscleId(10L));
    }

    @Test
    void shouldThrowWhenMuscleIdNotFound() {
        assertThrows(IllegalArgumentException.class, () -> MuscleEnum.fromMuscleId(99L));
    }

    @Test
    void shouldThrowWhenMuscleIdIsZero() {
        assertThrows(IllegalArgumentException.class, () -> MuscleEnum.fromMuscleId(0L));
    }
}
