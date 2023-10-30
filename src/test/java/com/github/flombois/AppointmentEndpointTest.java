package com.github.flombois;

import com.github.flombois.repositories.AppointmentRepository;

public abstract class AppointmentEndpointTest extends EndpointTest {

    @Override
    public String getEndpoint() {
        return AppointmentRepository.ENDPOINT;
    }
}
