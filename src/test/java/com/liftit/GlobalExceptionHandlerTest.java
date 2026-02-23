package com.liftit;

import com.liftit.user.Auth0Id;
import com.liftit.user.exception.DuplicateProfileException;
import com.liftit.user.exception.DuplicateUserException;
import com.liftit.user.exception.UnauthorizedException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Verifies that {@link GlobalExceptionHandler} maps domain exceptions to the
 * correct HTTP status codes, independent of any specific controller.
 */
class GlobalExceptionHandlerTest {

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(new ThrowingController())
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void shouldReturn409ForDuplicateUserException() throws Exception {
        ThrowingController.exceptionToThrow = DuplicateUserException.forAuth0Id(Auth0Id.of("auth0|test"));
        mockMvc.perform(get("/test")).andExpect(status().isConflict());
    }

    @Test
    void shouldReturn409ForDuplicateProfileException() throws Exception {
        ThrowingController.exceptionToThrow = DuplicateProfileException.forUser(1L);
        mockMvc.perform(get("/test")).andExpect(status().isConflict());
    }

    @Test
    void shouldReturn401ForUnauthorizedException() throws Exception {
        ThrowingController.exceptionToThrow = new UnauthorizedException();
        mockMvc.perform(get("/test")).andExpect(status().isUnauthorized());
    }

    @Test
    void shouldReturn400ForIllegalArgumentException() throws Exception {
        ThrowingController.exceptionToThrow = new IllegalArgumentException("bad input");
        mockMvc.perform(get("/test")).andExpect(status().isBadRequest());
    }

    /** Minimal controller that throws a configurable exception for each request. */
    @RestController
    static class ThrowingController {
        static RuntimeException exceptionToThrow;

        @GetMapping("/test")
        void throwException() {
            throw exceptionToThrow;
        }
    }
}
