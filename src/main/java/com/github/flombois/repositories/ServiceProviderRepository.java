package com.github.flombois.repositories;

import com.github.flombois.entities.ServiceProvider;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.UUID;

@RepositoryRestResource
public interface ServiceProviderRepository extends PagingAndSortingRepository<ServiceProvider, UUID> {
}
