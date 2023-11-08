package com.github.flombois.tests;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static com.github.flombois.exceptions.RestExceptionHandler.CONSTRAINT_VALIDATION_ERROR;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public interface CreateResourceTest extends ResourceTest {

    String getValidBody();

    String getInvalidBody();

    @Test
    @DisplayName("If the request is valid then respond with 201 CREATED")
    default void success() throws Exception {
        getMockMvc().perform(post(getEndpointUri())
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(getValidBody()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$").doesNotExist());
    }

    @Test
    @DisplayName("If the request body is missing then respond with 400 BAD REQUEST")
    default void withoutBody() throws Exception {
        getMockMvc().perform(post(getEndpointUri())
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("If the request body is incomplete then respond with 400 BAD REQUEST")
    default void withIncompleteBody() throws Exception {
        getMockMvc().perform(post(getEndpointUri())
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(CONSTRAINT_VALIDATION_ERROR));
    }

    @Test
    @DisplayName("If the request body is invalid then respond with 400 BAD REQUEST")
    default void withInvalidBody() throws Exception {
        getMockMvc().perform(post(getEndpointUri())
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(getInvalidBody()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(CONSTRAINT_VALIDATION_ERROR));
    }
}
