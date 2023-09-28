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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * End-to-end test for User endpoint
 */
@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Tests for user endpoint")
public class UserEndpointTests implements PostgresContainerTest {

    @Value("${spring.data.rest.base-path}")
    private String basePath;

    @Value("${spring.data.rest.default-page-size}")
    private int defaultPageSize;

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
            @Sql(scripts = "/truncate-users.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
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

            @Nested
            @Sql(scripts = "/insert-users.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
            @Sql(scripts = "/truncate-users.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
            @DisplayName("When user collection is requested")
            class FetchUserCollection {

                @Test
                @DisplayName("If the request is valid then return paginated results with 200 OK")
                void success() throws Exception {
                    mockMvc.perform(get(getEndpointUri()))
                            .andExpect(status().isOk())
                            // Ensure pagination is properly set
                            .andExpect(jsonPath("$.page.size").value(defaultPageSize))
                            .andExpect(jsonPath("$.page.totalElements").value(20))
                            .andExpect(jsonPath("$.page.totalPages").value(20 / defaultPageSize))
                            .andExpect(jsonPath("$.page.number").value(0))
                            // Check response body
                            .andExpect(jsonPath("$._embedded.users[0].username").value("test"))
                            .andExpect(jsonPath("$._embedded.users[1].username").value("tangocharlie"))
                            .andExpect(jsonPath("$._embedded.users[2].username").value("remco"))
                            .andExpect(jsonPath("$._embedded.users[3].username").value("virus55"))
                            .andExpect(jsonPath("$._embedded.users[4].username").value("johndoe"));
                }

                @Test
                @DisplayName("If the request is valid then return sorted by ascending username results with 200 OK")
                void sortByUsernameAsc() throws Exception {
                    mockMvc.perform(get(getEndpointUri()+"?sort=username,asc"))
                            .andExpect(status().isOk())
                            // Ensure pagination is properly set
                            .andExpect(jsonPath("$.page.size").value(defaultPageSize))
                            .andExpect(jsonPath("$.page.totalElements").value(20))
                            .andExpect(jsonPath("$.page.totalPages").value(20 / defaultPageSize))
                            .andExpect(jsonPath("$.page.number").value(0))
                            // Check response body
                            .andExpect(jsonPath("$._embedded.users[0].username").value("admin123"))
                            .andExpect(jsonPath("$._embedded.users[1].username").value("dragon99"))
                            .andExpect(jsonPath("$._embedded.users[2].username").value("elf77"))
                            .andExpect(jsonPath("$._embedded.users[3].username").value("goblin33"))
                            .andExpect(jsonPath("$._embedded.users[4].username").value("janedoe"));
                }

                @Test
                @DisplayName("If the request is valid then return sorted by descending username results with 200 OK")
                void sortByUsernameDesc() throws Exception {
                    mockMvc.perform(get(getEndpointUri()+"?sort=username,desc"))
                            .andExpect(status().isOk())
                            // Ensure pagination is properly set
                            .andExpect(jsonPath("$.page.size").value(defaultPageSize))
                            .andExpect(jsonPath("$.page.totalElements").value(20))
                            .andExpect(jsonPath("$.page.totalPages").value(20 / defaultPageSize))
                            .andExpect(jsonPath("$.page.number").value(0))
                            // Check response body
                            .andExpect(jsonPath("$._embedded.users[0].username").value("wizard55"))
                            .andExpect(jsonPath("$._embedded.users[1].username").value("warrior44"))
                            .andExpect(jsonPath("$._embedded.users[2].username").value("virus55"))
                            .andExpect(jsonPath("$._embedded.users[3].username").value("viper22"))
                            .andExpect(jsonPath("$._embedded.users[4].username").value("unicorn88"));
                }

            }

        }

    }

    String getEndpointUri() {
        return String.format("%s/%s", basePath, UserRepository.ENDPOINT);
    }
}
