package com.github.flombois;

import com.github.flombois.rest.CreateResourceTest;
import com.github.flombois.rest.DeleteResourceTest;
import com.github.flombois.rest.UpdateResourceTest;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * End-to-end test for Appointment endpoint
 */
@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext
@DisplayName("Tests for appointment endpoint")
public class AppointmentEndpointTests implements PostgresContainerTest {

    @Nested
    @WithMockUser
    @DisplayName("Given the user is authenticated")
    class Authenticated {

        @Nested
        @DisplayName("Given the user has sufficient privileges")
        class Authorized {

            @Nested
            @Sql(scripts = {"/insert-users.sql", "/insert-service-providers.sql", "/insert-appointments.sql"},
                    executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
            @Sql(scripts = {"/truncate-appointments.sql", "/truncate-service-providers.sql", "/truncate-users.sql"},
                    executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
            @DisplayName("When appointment creation is requested")
            class CreateAppointment extends AppointmentEndpointTest implements CreateResourceTest {

                @Override
                public String getValidBody() {
                    return """
                            {
                                "customer": "/users/aec4f0a1-d547-4a93-b201-dc6943739de0",
                                "serviceProvider": "/service-providers/78016474-5b3f-42e7-ab7b-a164adc95b0e",
                                "startDateTime": "2023-11-04T13:00:00+00:00",
                                "duration": 10
                            }
                            """;
                }

                @Override
                public String getInvalidBody() {
                    return """
                            {
                                "customer": "/users/aec4f0a1-d547-4a93-b201-dc6943739de0",
                                "serviceProvider": "/service-providers/78016474-5b3f-42e7-ab7b-a164adc95b0e",
                                "startDateTime": "2023-11-04T13:00:00+00:00",
                                "duration": 0
                            }
                            """;
                }

                @Test
                @DisplayName("If an appointment post-overlap responds with 409 CONFLICT")
                void withPostOverlappingAppointment() throws Exception {
                    final String body = """
                            {
                                "customer": "/users/d071c4e6-f2e3-4b57-b9d1-9af6d049a288",
                                "serviceProvider": "/service-providers/78016474-5b3f-42e7-ab7b-a164adc95b0e",
                                "startDateTime": "2023-10-30T08:30:00+00:00",
                                "duration": 15
                            }
                            """;
                    getMockMvc().perform(post(getEndpointUri())
                                    .with(csrf())
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(body))
                            .andExpect(status().isConflict())
                            .andExpect(jsonPath("$.message").value(DATA_INTEGRITY_VALIDATION_ERROR));
                }

                @Test
                @DisplayName("If an appointment pre-overlap responds with 409 CONFLICT")
                void withPreOverlappingAppointment() throws Exception {
                    final String body = """
                            {
                                "customer": "/users/d071c4e6-f2e3-4b57-b9d1-9af6d049a288",
                                "serviceProvider": "/service-providers/78016474-5b3f-42e7-ab7b-a164adc95b0e",
                                "startDateTime": "2023-10-30T07:45:00+00:00",
                                "duration": 30
                            }
                            """;
                    getMockMvc().perform(post(getEndpointUri())
                                    .with(csrf())
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(body))
                            .andExpect(status().isConflict())
                            .andExpect(jsonPath("$.message").value(DATA_INTEGRITY_VALIDATION_ERROR));
                }

                @Test
                @DisplayName("If the previous appointment is scheduled earlier then respond with 201 CREATED")
                void withPreviousAppointmentFinished() throws Exception {
                    final String body = """
                            {
                                "customer": "/users/d071c4e6-f2e3-4b57-b9d1-9af6d049a288",
                                "serviceProvider": "/service-providers/78016474-5b3f-42e7-ab7b-a164adc95b0e",
                                "startDateTime": "2023-10-30T09:00:00+00:00",
                                "duration": 30
                            }
                            """;
                    getMockMvc().perform(post(getEndpointUri())
                                    .with(csrf())
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(body))
                            .andExpect(status().isCreated())
                            .andExpect(jsonPath("$").doesNotExist());
                }

                @Test
                @DisplayName("If the next appointment is scheduled later then respond with 201 CREATED")
                void withNextAppointmentNotStarted() throws Exception {
                    final String body = """
                            {
                                "customer": "/users/d071c4e6-f2e3-4b57-b9d1-9af6d049a288",
                                "serviceProvider": "/service-providers/78016474-5b3f-42e7-ab7b-a164adc95b0e",
                                "startDateTime": "2023-10-30T10:00:00+00:00",
                                "duration": 30
                            }
                            """;
                    getMockMvc().perform(post(getEndpointUri())
                                    .with(csrf())
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(body))
                            .andExpect(status().isCreated())
                            .andExpect(jsonPath("$").doesNotExist());
                }
            }

            @Nested
            @Sql(scripts = {"/insert-users.sql", "/insert-service-providers.sql", "/insert-appointments.sql"},
                    executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
            @Sql(scripts = {"/truncate-appointments.sql", "/truncate-service-providers.sql", "/truncate-users.sql"},
                    executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
            @DisplayName("When appointment creation is requested")
            class UpdateAppointment  extends AppointmentEndpointTest implements UpdateResourceTest {

                @Override
                public String getValidBody() {
                    return """
                            {
                                "customer": "/users/aec4f0a1-d547-4a93-b201-dc6943739de0",
                                "serviceProvider": "/service-providers/78016474-5b3f-42e7-ab7b-a164adc95b0e",
                                "startDateTime": "2023-10-30T08:00:00+00",
                                "duration": 30
                            }
                            """;
                }

                @Override
                public String getInvalidBody() {
                    return """
                            {
                                "customer": "/users/aec4f0a1-d547-4a93-b201-dc6943739de0",
                                "serviceProvider": "/service-providers/78016474-5b3f-42e7-ab7b-a164adc95b0e",
                                "startDateTime": "2023-10-30T08:00:00+00",
                                "duration": 0
                            }
                            """;
                }

                @Override
                public UUID getValidUUID() {
                    return UUID.fromString("0114d846-4266-4fa8-b600-0c8e4916cc14");
                }
            }


            @Nested
            @Sql(scripts = {"/insert-users.sql", "/insert-service-providers.sql", "/insert-appointments.sql"},
                    executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
            @Sql(scripts = {"/truncate-appointments.sql", "/truncate-service-providers.sql", "/truncate-users.sql"},
                    executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
            @DisplayName("When appointment deletion is requested")
            class DeleteAppointment extends AppointmentEndpointTest implements DeleteResourceTest {

                @Override
                public UUID validUUID() {
                    return UUID.fromString("0114d846-4266-4fa8-b600-0c8e4916cc14");
                }

            }
        }
    }
}
