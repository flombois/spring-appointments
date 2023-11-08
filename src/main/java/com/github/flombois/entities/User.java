package com.github.flombois.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "users", schema = "public")
public class User extends PersistentEntity<UUID> {

    @NotBlank
    @Column(name = "username", unique = true, nullable = false)
    private String username;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public boolean equals(Object obj) {
        // Equality based on UUIDs
        return (obj instanceof User u && getId().equals(u.getId()));
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getUsername());
    }
}
