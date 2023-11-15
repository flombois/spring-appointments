package com.github.flombois;

import com.github.flombois.endpoints.AppointmentEndpointTest;
import com.github.flombois.endpoints.ServiceProviderEndpointTest;
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

                @Test
                @DisplayName("If the request is valid then return paginated results with 200 OK")
                void success() throws Exception {
                    checkPagination(getMockMvc().perform(get(getEndpointUri()))
                            .andExpect(status().isOk()))
                            .andDo(print())
                            // Check response body
                            .andExpect(jsonPath("$._embedded.serviceProviders[0].name").value("Flower shop"))
                            .andExpect(jsonPath("$._embedded.serviceProviders[1].name").value("Coffee House"))
                            .andExpect(jsonPath("$._embedded.serviceProviders[2].name").value("Tech Repair Shop"))
                            .andExpect(jsonPath("$._embedded.serviceProviders[3].name").value("Art Studio"))
                            .andExpect(jsonPath("$._embedded.serviceProviders[4].name").value("Fitness Center"));
                }

                @Test
                @DisplayName("If the request is valid then return sorted by ascending name results with 200 OK")
                void sortByNameAsc() throws Exception {
                    checkPagination(getMockMvc().perform(get(getEndpointUri()+"?sort=name,asc"))
                            .andExpect(status().isOk()))
                            // Check response body
                            .andExpect(jsonPath("$._embedded.serviceProviders[0].name").value("Art Studio"))
                            .andExpect(jsonPath("$._embedded.serviceProviders[1].name").value("Auto Repair Shop"))
                            .andExpect(jsonPath("$._embedded.serviceProviders[2].name").value("Bakery"))
                            .andExpect(jsonPath("$._embedded.serviceProviders[3].name").value("Bookstore"))
                            .andExpect(jsonPath("$._embedded.serviceProviders[4].name").value("Clothing Boutique"));
                }

                @Test
                @DisplayName("If the request is valid then return sorted by descending name results with 200 OK")
                void sortByNameDesc() throws Exception {
                    checkPagination(getMockMvc().perform(get(getEndpointUri()+"?sort=name,desc"))
                            .andExpect(status().isOk()))
                            // Check response body
                            .andExpect(jsonPath("$._embedded.serviceProviders[0].name").value("Yoga Studio"))
                            .andExpect(jsonPath("$._embedded.serviceProviders[1].name").value("Tech Repair Shop"))
                            .andExpect(jsonPath("$._embedded.serviceProviders[2].name").value("Spa and Wellness Center"))
                            .andExpect(jsonPath("$._embedded.serviceProviders[3].name").value("Restaurant"))
                            .andExpect(jsonPath("$._embedded.serviceProviders[4].name").value("Photography Studio"));
                }
            }

            @Nested
            @Sql(scripts = {"/insert-users.sql" ,"/insert-service-providers.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
            @Sql(scripts = {"/truncate-service-providers.sql", "/truncate-users.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
            @DisplayName("When service provider resource is requested")
            class FetchSingleServiceProvider extends ServiceProviderEndpointTest implements FetchSingleResourceTest {

                @Override
                public UUID validUUID() {
                    return UUID.fromString("78016474-5b3f-42e7-ab7b-a164adc95b0e");
                }


                @Test
                @DisplayName("If the service provider resource exist then return 200 OK")
                void success() throws Exception {
                    getMockMvc().perform(get(getResourceUri(validUUID()))
                                    .with(csrf()))
                            .andExpect(status().isOk())
                            .andExpect(jsonPath("$.name").value("Flower shop"));
                }

            }

            @Nested
            @Sql(scripts = {"/insert-users.sql" ,"/insert-service-providers.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
            @Sql(scripts = {"/truncate-service-providers.sql", "/truncate-users.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
            @DisplayName("When service provider resource is updated")
            class UpdateServiceProvider extends ServiceProviderEndpointTest implements UpdateResourceTest {


                @Override
                public String getValidBody() {
                    return """
                            {
                                "name": "New flower shop",
                                "description": "Under renovation",
                                "owner": "/users/a90b964c-90e2-4b1a-88af-3643b53c4789"
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

                @Override
                public UUID getValidUUID() {
                    return UUID.fromString("78016474-5b3f-42e7-ab7b-a164adc95b0e");
                }
            }

            @Nested
            @Sql(scripts = {"/insert-users.sql" ,"/insert-service-providers.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
            @Sql(scripts = {"/truncate-service-providers.sql", "/truncate-users.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
            @DisplayName("When service provider resource is updated")
            class DeleteServiceProvider extends ServiceProviderEndpointTest implements DeleteResourceTest {

                @Override
                public UUID validUUID() {
                    return UUID.fromString("78016474-5b3f-42e7-ab7b-a164adc95b0e");
                }

            }
        }
    }

    @Nested
    @DisplayName("Given the user is NOT authenticated")
    class UnAuthenticated extends ServiceProviderEndpointTest implements UnauthenticatedTest {

    }
}
