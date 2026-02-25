package com.liftit.muscle;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Validates that {@link MuscleEnum} constants are in sync with the {@code muscles}
 * reference table at application startup.
 *
 * <p>Throws {@link IllegalStateException} if the enum and table diverge — either
 * in count or in the set of IDs present. This ensures the Java enum and the
 * database seed never silently fall out of sync.
 */
@Component
class MuscleEnumValidator implements ApplicationRunner {

    private final MuscleRepository muscleRepository;

    MuscleEnumValidator(MuscleRepository muscleRepository) {
        this.muscleRepository = muscleRepository;
    }

    @Override
    public void run(ApplicationArguments args) {
        List<Muscle> dbMuscles = muscleRepository.findAll();
        Set<Long> enumIds = Stream.of(MuscleEnum.values())
                .map(MuscleEnum::getMuscleId)
                .collect(Collectors.toSet());
        Set<Long> dbIds = dbMuscles.stream()
                .map(Muscle::id)
                .collect(Collectors.toSet());

        if (!enumIds.equals(dbIds)) {
            throw new IllegalStateException(
                    "MuscleEnum is out of sync with the muscles table. "
                    + "Enum IDs: " + enumIds + ", DB IDs: " + dbIds);
        }
    }
}
