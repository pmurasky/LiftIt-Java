package com.liftit.exercise;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Validates that {@link ExerciseCategoryEnum} constants are in sync with the
 * {@code exercise_categories} reference table at application startup.
 *
 * <p>Throws {@link IllegalStateException} if the enum and table diverge — either
 * in count or in the set of IDs present. This ensures the Java enum and the
 * database seed never silently fall out of sync.
 */
@Component
class ExerciseCategoryEnumValidator implements ApplicationRunner {

    private final ExerciseCategoryRepository exerciseCategoryRepository;

    ExerciseCategoryEnumValidator(ExerciseCategoryRepository exerciseCategoryRepository) {
        this.exerciseCategoryRepository = exerciseCategoryRepository;
    }

    @Override
    public void run(ApplicationArguments args) {
        List<ExerciseCategory> dbCategories = exerciseCategoryRepository.findAll();
        Set<Long> enumIds = Stream.of(ExerciseCategoryEnum.values())
                .map(ExerciseCategoryEnum::getCategoryId)
                .collect(Collectors.toSet());
        Set<Long> dbIds = dbCategories.stream()
                .map(ExerciseCategory::id)
                .collect(Collectors.toSet());

        if (!enumIds.equals(dbIds)) {
            throw new IllegalStateException(
                    "ExerciseCategoryEnum is out of sync with the exercise_categories table. "
                    + "Enum IDs: " + enumIds + ", DB IDs: " + dbIds);
        }
    }
}
