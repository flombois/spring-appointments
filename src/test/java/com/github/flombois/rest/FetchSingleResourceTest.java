package com.github.flombois.rest;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public interface FetchSingleResourceTest extends ResourceTest {

    UUID validUUID();

    UUID notFoundUUID();

    @Test
    @DisplayName("If the resource does not exist then return 404 not found")
    default void notFound() throws Exception {
        getMockMvc().perform(get(getResourceUri(notFoundUUID()))
                        .with(csrf()))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("If the resource exists then ensure the self link is properly set")
    default void testSelfLink() throws Exception {
        getMockMvc().perform(get(getResourceUri(validUUID()))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._links.self.href").value(getResourceUrl(validUUID())));
    }


}
