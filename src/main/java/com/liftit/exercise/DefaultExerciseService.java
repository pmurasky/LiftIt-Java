package com.liftit.exercise;

import com.liftit.exercise.exception.DuplicateExerciseException;
import com.liftit.exercise.exception.ExerciseNotFoundException;
import com.liftit.exercise.exception.ExerciseOwnershipException;
import com.liftit.muscle.MuscleEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;

/**
 * Default implementation of {@link ExerciseService}.
 *
 * <p>Enforces ownership checks (users may only modify their own exercises),
 * name uniqueness, and delegates persistence to {@link ExerciseRepository}
 * and category lookup to {@link ExerciseCategoryRepository}.
 *
 * <p>The database assigns the real {@code BIGINT IDENTITY} primary key;
 * {@code 0L} is used for unsaved exercise IDs.
 */
@Service
public class DefaultExerciseService implements ExerciseService {

    private static final long UNASSIGNED_ID = 0L;

    private final ExerciseRepository exerciseRepository;
    private final ExerciseCategoryRepository categoryRepository;

    public DefaultExerciseService(
            ExerciseRepository exerciseRepository,
            ExerciseCategoryRepository categoryRepository) {
        if (exerciseRepository == null) {
            throw new IllegalArgumentException("exerciseRepository must not be null");
        }
        if (categoryRepository == null) {
            throw new IllegalArgumentException("categoryRepository must not be null");
        }
        this.exerciseRepository = exerciseRepository;
        this.categoryRepository = categoryRepository;
    }

    /** {@inheritDoc} */
    @Override
    public Exercise create(CreateExerciseRequest request, Long userId) {
        requireNonNull(request, "request");
        requireNonNull(userId, "userId");
        checkNameAvailable(request.name());
        return exerciseRepository.save(buildNewExercise(request, userId));
    }

    /** {@inheritDoc} */
    @Override
    public Exercise getById(Long id) {
        requireNonNull(id, "id");
        return exerciseRepository.findById(id)
                .orElseThrow(() -> new ExerciseNotFoundException(id));
    }

    /** {@inheritDoc} */
    @Override
    public Exercise update(Long id, UpdateExerciseRequest request, Long userId) {
        requireNonNull(id, "id");
        requireNonNull(request, "request");
        requireNonNull(userId, "userId");
        Exercise existing = exerciseRepository.findById(id)
                .orElseThrow(() -> new ExerciseNotFoundException(id));
        checkOwnership(existing, userId);
        checkNameAvailableForUpdate(request.name(), id);
        return exerciseRepository.save(applyUpdate(existing, request));
    }

    /** {@inheritDoc} */
    @Override
    public void delete(Long id, Long userId) {
        requireNonNull(id, "id");
        requireNonNull(userId, "userId");
        Exercise existing = exerciseRepository.findById(id)
                .orElseThrow(() -> new ExerciseNotFoundException(id));
        checkOwnership(existing, userId);
        exerciseRepository.delete(id);
    }

    /** {@inheritDoc} */
    @Override
    public Page<Exercise> list(ExerciseFilter filter, Pageable pageable) {
        requireNonNull(filter, "filter");
        requireNonNull(pageable, "pageable");
        return exerciseRepository.findAll(filter, pageable);
    }

    /** {@inheritDoc} */
    @Override
    public List<ExerciseCategory> getCategories() {
        return categoryRepository.findAll();
    }

    /** {@inheritDoc} */
    @Override
    public List<MuscleEnum> getMuscleGroups() {
        return Arrays.asList(MuscleEnum.values());
    }

    private Exercise buildNewExercise(CreateExerciseRequest request, Long userId) {
        Instant now = Instant.now();
        return new Exercise(UNASSIGNED_ID, request.name(), request.category(),
                request.muscleGroups(), now, userId, now, userId);
    }

    private Exercise applyUpdate(Exercise existing, UpdateExerciseRequest request) {
        Instant now = Instant.now();
        return new Exercise(existing.id(), request.name(), request.category(),
                request.muscleGroups(), existing.createdAt(), existing.createdBy(),
                now, existing.createdBy());
    }

    private void checkNameAvailable(String name) {
        if (exerciseRepository.findByName(name).isPresent()) {
            throw new DuplicateExerciseException(name);
        }
    }

    private void checkNameAvailableForUpdate(String name, Long excludeId) {
        exerciseRepository.findByName(name)
                .filter(existing -> !existing.id().equals(excludeId))
                .ifPresent(existing -> { throw new DuplicateExerciseException(name); });
    }

    private void checkOwnership(Exercise exercise, Long userId) {
        if (!exercise.createdBy().equals(userId)) {
            throw new ExerciseOwnershipException(exercise.id());
        }
    }

    private static void requireNonNull(Object value, String fieldName) {
        if (value == null) {
            throw new IllegalArgumentException(fieldName + " must not be null");
        }
    }
}
