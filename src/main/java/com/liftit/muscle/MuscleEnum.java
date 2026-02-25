package com.liftit.muscle;

/**
 * Enumeration of system-defined muscle groups.
 *
 * <p>Each constant carries the {@code muscleId} that corresponds to the matching
 * row in the {@code muscles} reference table. The mapping is validated at
 * application startup by {@code MuscleEnumValidator}.
 *
 * <p>IDs 1-99 are reserved for system-defined muscles. Any future muscle group
 * must be added both here and in a new Liquibase seed changeset.
 */
public enum MuscleEnum {

    ABDOMINALS(1L),
    BACK(2L),
    BICEPS(3L),
    CALVES(4L),
    CHEST(5L),
    FOREARMS(6L),
    NECK(7L),
    SHOULDERS(8L),
    THIGHS(9L),
    TRICEPS(10L);

    private final long muscleId;

    MuscleEnum(long muscleId) {
        this.muscleId = muscleId;
    }

    /**
     * Returns the {@code muscles.id} value this enum constant maps to.
     *
     * @return the database row ID for this muscle group
     */
    public long getMuscleId() {
        return muscleId;
    }

    /**
     * Looks up a {@code MuscleEnum} by its database row ID.
     *
     * @param id the {@code muscles.id} to look up
     * @return the matching {@code MuscleEnum} constant
     * @throws IllegalArgumentException if no constant matches the given ID
     */
    public static MuscleEnum fromMuscleId(long id) {
        for (MuscleEnum value : values()) {
            if (value.muscleId == id) {
                return value;
            }
        }
        throw new IllegalArgumentException("No MuscleEnum with muscleId: " + id);
    }
}
