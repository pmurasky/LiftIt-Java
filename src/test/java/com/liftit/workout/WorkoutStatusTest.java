package com.liftit.workout;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class WorkoutStatusTest {

    @Test
    void shouldHaveTwoStatuses() {
        // Given / When / Then
        assertEquals(2, WorkoutStatus.values().length);
    }

    @Test
    void shouldDefineInProgressAndCompleted() {
        // Given / When / Then
        assertEquals(WorkoutStatus.IN_PROGRESS, WorkoutStatus.valueOf("IN_PROGRESS"));
        assertEquals(WorkoutStatus.COMPLETED, WorkoutStatus.valueOf("COMPLETED"));
    }
}
