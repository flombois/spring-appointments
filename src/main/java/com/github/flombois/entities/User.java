package com.github.flombois.entities;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "users", schema = "public")
public class User extends PersistentEntity<UUID> {

    @Column(name = "username", unique = true)
    private String username;

}
