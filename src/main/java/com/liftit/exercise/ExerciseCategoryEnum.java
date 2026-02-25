package com.liftit.exercise;

/**
 * Enumeration of system-defined exercise categories.
 *
 * <p>Each constant carries the {@code categoryId} that corresponds to the matching
 * row in the {@code exercise_categories} reference table. The mapping is validated
 * at application startup by {@code ExerciseCategoryEnumValidator}.
 *
 * <p>IDs 1-99 are reserved for system-defined categories. Any future category
 * must be added both here and in a new Liquibase seed changeset.
 */
public enum ExerciseCategoryEnum {

    STRENGTH(1L);

    private final long categoryId;

    ExerciseCategoryEnum(long categoryId) {
        this.categoryId = categoryId;
    }

    /**
     * Returns the {@code exercise_categories.id} value this enum constant maps to.
     *
     * @return the database row ID for this exercise category
     */
    public long getCategoryId() {
        return categoryId;
    }

    /**
     * Looks up an {@code ExerciseCategoryEnum} by its database row ID.
     *
     * @param id the {@code exercise_categories.id} to look up
     * @return the matching {@code ExerciseCategoryEnum} constant
     * @throws IllegalArgumentException if no constant matches the given ID
     */
    public static ExerciseCategoryEnum fromCategoryId(long id) {
        for (ExerciseCategoryEnum value : values()) {
            if (value.categoryId == id) {
                return value;
            }
        }
        throw new IllegalArgumentException("No ExerciseCategoryEnum with categoryId: " + id);
    }
}
