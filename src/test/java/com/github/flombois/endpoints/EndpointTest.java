package com.github.flombois.endpoints;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

public abstract class EndpointTest {

    @Autowired
    private MockMvc mockMvc;

    @Value("${spring.data.rest.base-path}")
    private String basePath;

    @Value("${spring.data.rest.default-page-size}")
    private int defaultPageSize;

    public abstract String getEndpoint();

    public MockMvc getMockMvc() {
        return mockMvc;
    }

    public String getBasePath() {
        return basePath;
    }

    public int getDefaultPageSize() {
        return defaultPageSize;
    }

    public String getEndpointUri() {
        return String.format("%s/%s", getBasePath(), getEndpoint());
    }

    public String getSearchEndpointUri(String search) {
        return String.format("%s/search/%s", getEndpointUri(), search);
    }

    public String getResourceUri(UUID uuid) {
        return String.format("%s/%s", getEndpointUri(), uuid.toString());
    }

    public String getResourceUrl(UUID uuid) {
        return String.format("http://localhost%s", getResourceUri(uuid));
    }
}
