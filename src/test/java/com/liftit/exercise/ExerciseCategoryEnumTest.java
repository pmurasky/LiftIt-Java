package com.liftit.exercise;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ExerciseCategoryEnumTest {

    @Test
    void shouldHaveOneValue() {
        assertEquals(1, ExerciseCategoryEnum.values().length);
    }

    @Test
    void shouldHaveCorrectCategoryIdForStrength() {
        assertEquals(1L, ExerciseCategoryEnum.STRENGTH.getCategoryId());
    }

    @Test
    void shouldLookUpStrengthByCategoryId() {
        assertEquals(ExerciseCategoryEnum.STRENGTH, ExerciseCategoryEnum.fromCategoryId(1L));
    }

    @Test
    void shouldThrowWhenCategoryIdNotFound() {
        assertThrows(IllegalArgumentException.class, () -> ExerciseCategoryEnum.fromCategoryId(99L));
    }

    @Test
    void shouldThrowWhenCategoryIdIsZero() {
        assertThrows(IllegalArgumentException.class, () -> ExerciseCategoryEnum.fromCategoryId(0L));
    }
}
