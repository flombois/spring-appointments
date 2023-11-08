package com.github.flombois.tests;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.util.UUID;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public interface UpdateResourceTest extends CreateResourceTest {

    UUID getValidUUID();

    @Test
    @DisplayName("If the request is valid then respond with 204 NO CONTENT")
    default void success() throws Exception {
        getMockMvc().perform(put(getResourceUri(getValidUUID()))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(getValidBody()))
                .andExpect(status().isNoContent())
                .andExpect(jsonPath("$").doesNotExist());
    }


}
