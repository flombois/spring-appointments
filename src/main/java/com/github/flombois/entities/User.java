package com.github.flombois.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

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
}
