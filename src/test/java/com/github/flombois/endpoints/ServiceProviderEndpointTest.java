package com.github.flombois.endpoints;

import com.github.flombois.repositories.ServiceProviderRepository;

public abstract class ServiceProviderEndpointTest extends EndpointTest {

    @Override
    public String getEndpoint() {
        return ServiceProviderRepository.ENDPOINT;
    }
}
