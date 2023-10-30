package com.github.flombois;

import com.github.flombois.rest.CreateResourceTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;

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
            @DisplayName("When service provider creation is requested")
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
            }
        }
    }
}
