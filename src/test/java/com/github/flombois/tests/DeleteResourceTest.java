package com.github.flombois.tests;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


import java.util.UUID;

public interface DeleteResourceTest extends ResourceTest {

    UUID validUUID();

    default UUID notFoundUUID() {
        return UUID.randomUUID();
    }

    @Test
    @DisplayName("If the resource exists then return 204 NO CONTENT")
    default void success() throws Exception {
        getMockMvc().perform(delete(getResourceUri(validUUID()))
                        .with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("If the resource does not exist then return 404 NOT FOUND")
    default void notFound() throws Exception {
        getMockMvc().perform(delete(getResourceUri(notFoundUUID()))
                        .with(csrf()))
                .andExpect(status().isNotFound());
    }
}
