package com.github.flombois.rest;

import org.springframework.test.web.servlet.MockMvc;

public interface ResourceTest {

    MockMvc getMockMvc();

    String getEndpointUri();
}
