package com.github.flombois.repositories;

import com.github.flombois.entities.ServiceProvider;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.UUID;

import static com.github.flombois.repositories.ServiceProviderRepository.ENDPOINT;

@RepositoryRestResource(path = ENDPOINT)
public interface ServiceProviderRepository extends CrudRepository<ServiceProvider, UUID>, PagingAndSortingRepository<ServiceProvider, UUID> {
    String ENDPOINT = "service-providers";
}
