package com.github.flombois.tests;

import org.apache.logging.log4j.util.Strings;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.util.UUID;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public interface UnauthenticatedTest extends ResourceTest {

    @Test
    @DisplayName("If a POST request if performed to create a new resource then responds with 401 UNAUTHORIZED")
    default void create() throws Exception {
        getMockMvc().perform(post(getEndpointUri()).with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(getCreateBody()))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$").doesNotExist());
    }

    @Test
    @DisplayName("If a GET request if performed to fetch a resource collection then responds with 401 UNAUTHORIZED")
    default void fetchCollection() throws Exception {
        getMockMvc().perform(get(getEndpointUri()).with(csrf()))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$").doesNotExist());
    }

    @Test
    @DisplayName("If a GET request if performed to fetch a single collection then responds with 401 UNAUTHORIZED")
    default void fetchSingleResource() throws Exception {
        getMockMvc().perform(get(String.format("%s/%s", getEndpointUri(), UUID.randomUUID())).with(csrf()))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$").doesNotExist());
    }

    @Test
    @DisplayName("If a PUT request if performed to update a resource then responds with 401 UNAUTHORIZED")
    default void updateResource() throws Exception {
        getMockMvc().perform(put(getEndpointUri()).with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(getUpdateBody()))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$").doesNotExist());
    }

    @Test
    @DisplayName("If a DELETE request if performed to delete a resource then responds with 401 UNAUTHORIZED")
    default void deleteResource() throws Exception {
        getMockMvc().perform(delete(getEndpointUri()).with(csrf()))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$").doesNotExist());
    }

    default String getCreateBody() {
        return getRequestBody();
    }

    default String getUpdateBody() {
        return getRequestBody();
    }


    default String getRequestBody() {
        return Strings.EMPTY;
    }
}
