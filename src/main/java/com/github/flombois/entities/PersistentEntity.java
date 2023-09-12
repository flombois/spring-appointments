package com.github.flombois.entities;

import jakarta.persistence.*;

@MappedSuperclass
public abstract class PersistentEntity<T> {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private T id;
}
