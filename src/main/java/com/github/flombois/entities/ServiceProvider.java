package com.github.flombois.entities;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "service-providers", schema = "public")
public class ServiceProvider extends PersistentEntity<UUID> {

    @Column(name = "name", unique = true, nullable = false)
    private String name;

    @Column(name = "description", nullable = false)
    private String description;

    @ManyToOne
    private User owner;
}
