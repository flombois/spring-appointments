package com.github.flombois;

import com.github.flombois.rest.CreateResourceTest;
import com.github.flombois.rest.FetchResourceCollectionTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.ResultActions;

import static com.github.flombois.exceptions.RestExceptionHandler.DATA_INTEGRITY_VALIDATION_ERROR;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * End-to-end test for Service Provider endpoint
 */
@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext
@DisplayName("Tests for service provider endpoint")
public class ServiceProviderEndpointTests implements PostgresContainerTest {


    @Nested
    @WithMockUser
    @DisplayName("Given the user is authenticated")
    class Authenticated {

        @Nested
        @DisplayName("Given the user has sufficient privileges")
        class Authorized {

            @Nested
            @Sql(scripts = {"/insert-users.sql" ,"/insert-service-providers.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
            @Sql(scripts = {"/truncate-service-providers.sql", "/truncate-users.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
            @DisplayName("When service provider creation is requested")
            class CreateServiceProvider extends ServiceProviderEndpointTest implements CreateResourceTest {

                @Test
                @DisplayName("If the specified owner ID do not exists respond with 409 CONFLICT")
                void missingOwner() throws Exception {
                    final String body = """
                            {
                                "name": "test name",
                                "description": "test description",
                                "owner": "/users/c7861481-9e24-45a7-9df8-55f0547e6915"
                            }
                            """;
                    getMockMvc().perform(post(getEndpointUri())
                                    .with(csrf())
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(body))
                            .andDo(print())
                            .andExpect(status().isConflict())
                            .andExpect(jsonPath("$.message").value(DATA_INTEGRITY_VALIDATION_ERROR));
                }

                @Override
                public String getValidBody() {
                    return  """
                            {
                                "name": "test name",
                                "description": "test description",
                                "owner": "/users/aec4f0a1-d547-4a93-b201-dc6943739de0"
                            }
                            """;
                }

                @Override
                public String getInvalidBody() {
                    return  """
                            {
                                "name": "",
                                "description": "test description",
                                "owner": "/users/aec4f0a1-d547-4a93-b201-dc6943739de0"
                            }
                            """;
                }
            }

            @Nested
            @Sql(scripts = {"/insert-users.sql" ,"/insert-service-providers.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
            @Sql(scripts = {"/truncate-service-providers.sql", "/truncate-users.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
            @DisplayName("When service provider collection is requested")
            class FetchServiceProviderCollection extends ServiceProviderEndpointTest implements FetchResourceCollectionTest {

                @Override
                public void successValidation(ResultActions resultActions) throws Exception {

                }
            }

        }
    }
}
