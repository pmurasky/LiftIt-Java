package com.liftit.workout;

import com.liftit.workout.exception.WorkoutAlreadyCompletedException;
import com.liftit.workout.exception.WorkoutNotFoundException;
import com.liftit.workout.exception.WorkoutOwnershipException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

/**
 * Default implementation of {@link WorkoutService}.
 *
 * <p>Enforces ownership checks, lifecycle state transitions (IN_PROGRESS → COMPLETED),
 * and delegates persistence to {@link WorkoutRepository}.
 *
 * <p>The database assigns the real {@code BIGINT IDENTITY} primary key;
 * {@code 0L} is used for unsaved workout and exercise IDs.
 */
@Service
public class DefaultWorkoutService implements WorkoutService {

    private static final long UNASSIGNED_ID = 0L;

    private final WorkoutRepository workoutRepository;

    public DefaultWorkoutService(WorkoutRepository workoutRepository) {
        if (workoutRepository == null) {
            throw new IllegalArgumentException("workoutRepository must not be null");
        }
        this.workoutRepository = workoutRepository;
    }

    /** {@inheritDoc} */
    @Override
    public Workout start(Long userId, String notes) {
        requireNonNull(userId, "userId");
        Instant now = Instant.now();
        Workout newWorkout = new Workout(UNASSIGNED_ID, userId,
                now, null, WorkoutStatus.IN_PROGRESS, notes,
                List.of(), now, userId, now, userId);
        return workoutRepository.save(newWorkout);
    }

    /** {@inheritDoc} */
    @Override
    public Workout getById(Long id) {
        requireNonNull(id, "id");
        return workoutRepository.findById(id)
                .orElseThrow(() -> new WorkoutNotFoundException(id));
    }

    /** {@inheritDoc} */
    @Override
    public Page<Workout> listByUser(Long userId, Pageable pageable) {
        requireNonNull(userId, "userId");
        requireNonNull(pageable, "pageable");
        return workoutRepository.findByUserId(userId, pageable);
    }

    /** {@inheritDoc} */
    @Override
    public Workout addExercise(Long workoutId, WorkoutExercise exercise, Long userId) {
        requireNonNull(workoutId, "workoutId");
        requireNonNull(exercise, "exercise");
        requireNonNull(userId, "userId");
        Workout workout = requireOwned(workoutId, userId);
        checkNotCompleted(workout);
        return workoutRepository.save(workout.withExercise(exercise));
    }

    /** {@inheritDoc} */
    @Override
    public Workout complete(Long workoutId, Long userId) {
        requireNonNull(workoutId, "workoutId");
        requireNonNull(userId, "userId");
        Workout workout = requireOwned(workoutId, userId);
        checkNotCompleted(workout);
        return workoutRepository.save(workout.complete());
    }

    /** {@inheritDoc} */
    @Override
    public void delete(Long workoutId, Long userId) {
        requireNonNull(workoutId, "workoutId");
        requireNonNull(userId, "userId");
        requireOwned(workoutId, userId);
        workoutRepository.delete(workoutId);
    }

    private Workout requireOwned(Long workoutId, Long userId) {
        Workout workout = workoutRepository.findById(workoutId)
                .orElseThrow(() -> new WorkoutNotFoundException(workoutId));
        if (!workout.userId().equals(userId)) {
            throw new WorkoutOwnershipException(workoutId, userId);
        }
        return workout;
    }

    private void checkNotCompleted(Workout workout) {
        if (!workout.isInProgress()) {
            throw new WorkoutAlreadyCompletedException(workout.id());
        }
    }

    private static void requireNonNull(Object value, String fieldName) {
        if (value == null) {
            throw new IllegalArgumentException(fieldName + " must not be null");
        }
    }
}
