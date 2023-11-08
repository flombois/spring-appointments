package com.github.flombois.endpoints;

import com.github.flombois.repositories.AppointmentRepository;

public abstract class AppointmentEndpointTest extends EndpointTest {

    @Override
    public String getEndpoint() {
        return AppointmentRepository.ENDPOINT;
    }
}
