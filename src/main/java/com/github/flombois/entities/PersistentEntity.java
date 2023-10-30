package com.github.flombois.entities;

import jakarta.persistence.*;

import java.io.Serializable;
import java.util.UUID;

@MappedSuperclass
public abstract class PersistentEntity<T extends Serializable> {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private T id;

    public T getId() {
        return id;
    }
}
