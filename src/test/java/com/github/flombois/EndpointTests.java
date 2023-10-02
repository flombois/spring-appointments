package com.github.flombois;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

public abstract class EndpointTests {

    @Autowired
    private MockMvc mockMvc;

    @Value("${spring.data.rest.base-path}")
    private String basePath;

    @Value("${spring.data.rest.default-page-size}")
    private int defaultPageSize;

    protected abstract String getEndpoint();

    protected MockMvc getMockMvc() {
        return mockMvc;
    }

    protected String getBasePath() {
        return basePath;
    }

    protected int getDefaultPageSize() {
        return defaultPageSize;
    }

    protected String getEndpointUri() {
        return String.format("%s/%s", basePath, getEndpoint());
    }

    protected String getResourceUri(UUID uuid) {
        return String.format("%s/%s", getEndpointUri(), uuid.toString());
    }
}
