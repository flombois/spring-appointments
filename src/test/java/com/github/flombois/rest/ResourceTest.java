package com.github.flombois.rest;

import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

public interface ResourceTest {

    MockMvc getMockMvc();

    String getEndpointUri();

    String getResourceUri(UUID uuid);

    String getResourceUrl(UUID uuid);
}
