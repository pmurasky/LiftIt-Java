package com.liftit.workout;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

class WeightTest {

    private static final double DELTA = 0.001;

    @Test
    void shouldCreateWeightWithValueAndUnit() {
        // Given / When
        Weight weight = new Weight(100.0, WeightUnit.LBS);

        // Then
        assertEquals(100.0, weight.value(), DELTA);
        assertEquals(WeightUnit.LBS, weight.unit());
    }

    @Test
    void shouldThrowWhenUnitIsNull() {
        assertThrows(IllegalArgumentException.class, () -> new Weight(100.0, null));
    }

    @Test
    void shouldThrowWhenValueIsNegative() {
        assertThrows(IllegalArgumentException.class, () -> new Weight(-1.0, WeightUnit.LBS));
    }

    @Test
    void shouldAllowZeroWeight() {
        // Given / When
        Weight weight = new Weight(0.0, WeightUnit.LBS);

        // Then
        assertEquals(0.0, weight.value(), DELTA);
    }

    @Test
    void shouldConvertLbsToKg() {
        // Given
        Weight lbs = new Weight(220.462, WeightUnit.LBS);

        // When
        Weight kg = lbs.convertTo(WeightUnit.KG);

        // Then
        assertEquals(WeightUnit.KG, kg.unit());
        assertEquals(100.0, kg.value(), DELTA);
    }

    @Test
    void shouldConvertKgToLbs() {
        // Given
        Weight kg = new Weight(100.0, WeightUnit.KG);

        // When
        Weight lbs = kg.convertTo(WeightUnit.LBS);

        // Then
        assertEquals(WeightUnit.LBS, lbs.unit());
        assertEquals(220.462, lbs.value(), DELTA);
    }

    @Test
    void shouldReturnSameInstanceWhenConvertingToSameUnit() {
        // Given
        Weight weight = new Weight(100.0, WeightUnit.LBS);

        // When
        Weight result = weight.convertTo(WeightUnit.LBS);

        // Then
        assertSame(weight, result);
    }

    @Test
    void shouldThrowWhenConvertingToNullUnit() {
        // Given
        Weight weight = new Weight(100.0, WeightUnit.LBS);

        // When / Then
        assertThrows(IllegalArgumentException.class, () -> weight.convertTo(null));
    }
}
