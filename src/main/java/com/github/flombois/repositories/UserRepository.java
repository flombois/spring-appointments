package com.github.flombois.repositories;

import com.github.flombois.entities.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.UUID;

import static com.github.flombois.repositories.UserRepository.ENDPOINT;

@RepositoryRestResource(path = ENDPOINT)
public interface UserRepository extends CrudRepository<User, UUID> {

    String ENDPOINT = "users";

}
