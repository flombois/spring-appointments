package com.github.flombois;

import com.github.flombois.repositories.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import static com.github.flombois.exceptions.RestExceptionHandler.CONSTRAINT_VALIDATION_ERROR;
import static com.github.flombois.exceptions.RestExceptionHandler.DATA_INTEGRITY_VALIDATION_ERROR;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * End-to-end test for User endpoint
 */
@SpringBootTest
@AutoConfigureMockMvc
@Sql(scripts = "/truncate-users.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@DisplayName("Tests for user endpoint")
public class UserEndpointTests implements PostgresContainerTest {

    @Value("${spring.data.rest.base-path}")
    private String basePath;

    @Autowired
    private MockMvc mockMvc;

    @Nested
    @WithMockUser
    @DisplayName("Given the user is authenticated")
    class Authenticated {

        @Nested
        @DisplayName("Given the user has sufficient privileges")
        class Authorized {

            @Nested
            @DisplayName("When user creation is requested")
            class CreateUser {

                @Test
                @DisplayName("If the request is valid then respond with 201 CREATED")
                void success() throws Exception {
                    final String body = """
                            {
                                "username": "test"
                            }
                            """;
                    mockMvc.perform(post(getEndpointUri())
                                    .with(csrf())
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(body))
                            .andExpect(status().isCreated())
                            .andExpect(jsonPath("$").doesNotExist());
                }

                @Test
                @DisplayName("If the request body is missing then respond with 400 BAD REQUEST")
                void withoutBody() throws Exception {
                    mockMvc.perform(post(getEndpointUri())
                                    .with(csrf())
                                    .contentType(MediaType.APPLICATION_JSON))
                            .andExpect(status().isBadRequest());
                }

                @Test
                @DisplayName("If the request body is incomplete then respond with 400 BAD REQUEST")
                void withIncompleteBody() throws Exception {
                    mockMvc.perform(post(getEndpointUri())
                                    .with(csrf())
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content("{}"))
                            .andExpect(status().isBadRequest())
                            .andExpect(jsonPath("$.message").value(CONSTRAINT_VALIDATION_ERROR));
                }

                @Test
                @DisplayName("If the request body is invalid then respond with 400 BAD REQUEST")
                void withInvalidBody() throws Exception {
                    final String body = """
                            {
                                "username": ""
                            }
                            """;
                    mockMvc.perform(post(getEndpointUri())
                                    .with(csrf())
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(body))
                            .andExpect(status().isBadRequest())
                            .andExpect(jsonPath("$.message").value(CONSTRAINT_VALIDATION_ERROR));
                }

                @Test
                @Sql(scripts = "/insert-users.sql")
                @DisplayName("If a user with the supplied username already exists then respond with 409 CONFLICT")
                void withAlreadyExistingUsername() throws Exception {
                    final String body = """
                            {
                                "username": "test"
                            }
                            """;
                    mockMvc.perform(post(getEndpointUri())
                                    .with(csrf())
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(body))
                            .andExpect(status().isConflict())
                            .andExpect(jsonPath("$.message").value(DATA_INTEGRITY_VALIDATION_ERROR));
                }
            }

        }

    }

    String getEndpointUri() {
        return String.format("%s/%s", basePath, UserRepository.ENDPOINT);
    }
}
