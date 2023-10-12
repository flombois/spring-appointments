package com.github.flombois.entities;

import jakarta.persistence.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "appointments", schema = "public")
public class Appointment extends PersistentEntity<UUID> {

    @ManyToOne
    @JoinColumn(name = "customer")
    private User customer;

    @ManyToOne
    @JoinColumn(name = "serviceProvider")
    private ServiceProvider serviceProvider;

    @Column(name = "startDateTime", columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private OffsetDateTime startDateTime;

    @Column(name = "duration")
    private short duration;
}
