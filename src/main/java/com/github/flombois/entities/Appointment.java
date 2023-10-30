package com.github.flombois.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "appointments", schema = "public")
public class Appointment extends PersistentEntity<UUID> {

    @ManyToOne
    @JoinColumn(name = "customer", nullable = false)
    private User customer;

    @ManyToOne
    @JoinColumn(name = "service_provider", nullable = false)
    private ServiceProvider serviceProvider;

    @Column(name = "start_datetime", columnDefinition = "TIMESTAMP WITH TIME ZONE", nullable = false)
    private OffsetDateTime startDateTime;

    // Minimum duration of an appointment is 5 minutes
    @Min(5)
    @Column(name = "duration", nullable = false)
    private short duration;

    public User getCustomer() {
        return customer;
    }

    public void setCustomer(User customer) {
        this.customer = customer;
    }

    public ServiceProvider getServiceProvider() {
        return serviceProvider;
    }

    public void setServiceProvider(ServiceProvider serviceProvider) {
        this.serviceProvider = serviceProvider;
    }

    public OffsetDateTime getStartDateTime() {
        return startDateTime;
    }

    public void setStartDateTime(OffsetDateTime startDateTime) {
        this.startDateTime = startDateTime;
    }

    public short getDuration() {
        return duration;
    }

    public void setDuration(short duration) {
        this.duration = duration;
    }
}
