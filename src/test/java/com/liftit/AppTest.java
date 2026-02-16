package com.liftit;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AppTest {
    @Test
    void greetingReturnsExpectedMessage() {
        assertEquals("LiftIt Java starter is running.", App.greeting());
    }
}
