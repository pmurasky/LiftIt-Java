package com.liftit;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AppTest {
    @Test
    void greetingReturnsExpectedMessage() {
        assertThat(App.greeting()).isEqualTo("LiftIt Java starter is running.");
    }
}
