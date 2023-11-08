package com.github.flombois.endpoints;

import com.github.flombois.repositories.UserRepository;

public class UserEndpointTest extends EndpointTest {
    @Override
    public String getEndpoint() {
        return UserRepository.ENDPOINT;
    }
}
