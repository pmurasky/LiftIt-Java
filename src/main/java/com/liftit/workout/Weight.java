package com.liftit.workout;

/**
 * Immutable value object representing a weight with a unit.
 *
 * <p>Supports conversion between {@link WeightUnit#LBS} and {@link WeightUnit#KG}.
 * The conversion factor used is: 1 kg = 2.20462 lbs.
 *
 * @param value the numeric weight value; must be &gt;= 0
 * @param unit  the unit of measurement; must not be null
 */
public record Weight(double value, WeightUnit unit) {

    private static final double LBS_PER_KG = 2.20462;

    /**
     * Compact constructor — validates fields.
     *
     * @throws IllegalArgumentException if value is negative or unit is null
     */
    public Weight {
        if (unit == null) {
            throw new IllegalArgumentException("Weight.unit must not be null");
        }
        if (value < 0) {
            throw new IllegalArgumentException("Weight.value must not be negative");
        }
    }

    /**
     * Converts this weight to the given target unit.
     *
     * <p>Returns {@code this} if the target unit is the same as the current unit.
     *
     * @param targetUnit the unit to convert to; must not be null
     * @return a new {@code Weight} in the target unit, or {@code this} if no conversion needed
     */
    public Weight convertTo(WeightUnit targetUnit) {
        if (targetUnit == null) {
            throw new IllegalArgumentException("targetUnit must not be null");
        }
        if (targetUnit == unit) {
            return this;
        }
        return targetUnit == WeightUnit.KG
                ? new Weight(value / LBS_PER_KG, WeightUnit.KG)
                : new Weight(value * LBS_PER_KG, WeightUnit.LBS);
    }
}
