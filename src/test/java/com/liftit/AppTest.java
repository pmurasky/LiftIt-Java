package com.liftit;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AppTest {

    @Test
    void appClassExists() {
        // Given / When
        App app = new App();

        // Then - confirms the main application class is instantiable (unit test, no Spring context)
        assertThat(app).isNotNull();
    }
}
