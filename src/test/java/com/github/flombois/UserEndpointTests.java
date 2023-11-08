package com.github.flombois;

import com.github.flombois.endpoints.AppointmentEndpointTest;
import com.github.flombois.endpoints.UserEndpointTest;
import com.github.flombois.tests.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;

import java.util.UUID;

import static com.github.flombois.exceptions.RestExceptionHandler.DATA_INTEGRITY_VALIDATION_ERROR;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * End-to-end test for User endpoint
 */
@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext
@DisplayName("Tests for user endpoint")
public class UserEndpointTests implements PostgresContainerTest {

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
            class CreateUser extends UserEndpointTest implements CreateResourceTest {

                @Override
                public String getValidBody() {
                    return """
                            {
                                "username": "test"
                            }
                            """;
                }

                @Override
                public String getInvalidBody() {
                    return """
                            {
                                "username": ""
                            }
                            """;
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
                    getMockMvc().perform(post(getEndpointUri())
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
            class FetchUserCollection extends UserEndpointTest implements FetchResourceCollectionTest {

                @Test
                @DisplayName("If the request is valid then return paginated results with 200 OK")
                void success() throws Exception {
                   checkPagination(getMockMvc().perform(get(getEndpointUri()))
                            .andExpect(status().isOk()))
                            .andExpect(jsonPath("$._embedded.users[0].username").value("test"))
                            .andExpect(jsonPath("$._embedded.users[1].username").value("tangocharlie"))
                            .andExpect(jsonPath("$._embedded.users[2].username").value("remco"))
                            .andExpect(jsonPath("$._embedded.users[3].username").value("virus55"))
                            .andExpect(jsonPath("$._embedded.users[4].username").value("johndoe"));
                }


                @Test
                @DisplayName("If the request is valid then return sorted by ascending username results with 200 OK")
                void sortByUsernameAsc() throws Exception {
                    checkPagination(getMockMvc().perform(get(getEndpointUri()+"?sort=username,asc"))
                            .andExpect(status().isOk()))
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
                    checkPagination(getMockMvc().perform(get(getEndpointUri()+"?sort=username,desc"))
                            .andExpect(status().isOk()))
                            // Check response body
                            .andExpect(jsonPath("$._embedded.users[0].username").value("wizard55"))
                            .andExpect(jsonPath("$._embedded.users[1].username").value("warrior44"))
                            .andExpect(jsonPath("$._embedded.users[2].username").value("virus55"))
                            .andExpect(jsonPath("$._embedded.users[3].username").value("viper22"))
                            .andExpect(jsonPath("$._embedded.users[4].username").value("unicorn88"));
                }

            }

            @Nested
            @Sql(scripts = "/insert-users.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
            @Sql(scripts = "/truncate-users.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
            @DisplayName("When a user resource is requested")
            class FetchSingleUser extends UserEndpointTest implements FetchSingleResourceTest {

                @Test
                @DisplayName("If the user resource exist then return 200 OK")
                void success() throws Exception {
                    getMockMvc().perform(get(getResourceUri(validUUID()))
                                    .with(csrf()))
                            .andExpect(status().isOk())
                            .andExpect(jsonPath("$.username").value("test"));
                }

                @Override
                public UUID validUUID() {
                    return UUID.fromString("aec4f0a1-d547-4a93-b201-dc6943739de0");
                }

                @Override
                public UUID notFoundUUID() {
                    return UUID.randomUUID();
                }
            }

            @Nested
            @Sql(scripts = "/insert-users.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
            @Sql(scripts = "/truncate-users.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
            @DisplayName("When a user resource update is requested")
            class UpdateUser extends UserEndpointTest implements UpdateResourceTest {

                @Override
                public String getValidBody() {
                    return """
                            {
                                "username": "updated username"
                            }
                            """;
                }

                @Override
                public String getInvalidBody() {
                    return """
                            {
                                "username": ""
                            }
                            """;
                }

                @Override
                public UUID getValidUUID() {
                    return UUID.fromString("aec4f0a1-d547-4a93-b201-dc6943739de0");
                }
            }

            @Nested
            @Sql(scripts = "/insert-users.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
            @Sql(scripts = "/truncate-users.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
            @DisplayName("When a user resource deletion is requested")
            class DeleteUser extends UserEndpointTest implements DeleteResourceTest {

                @Override
                public UUID validUUID() {
                    return UUID.fromString("aec4f0a1-d547-4a93-b201-dc6943739de0");
                }

            }
        }

    }

    @Nested
    @DisplayName("Given the user is NOT authenticated")
    class UnAuthenticated extends UserEndpointTest implements UnauthenticatedTest {

    }
}
