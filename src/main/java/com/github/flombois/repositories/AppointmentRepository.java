package com.github.flombois.repositories;

import com.github.flombois.entities.Appointment;
import com.github.flombois.entities.ServiceProvider;
import jakarta.persistence.TemporalType;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.Temporal;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.OffsetDateTime;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static com.github.flombois.repositories.AppointmentRepository.ENDPOINT;

@RepositoryRestResource(path = ENDPOINT)
public interface AppointmentRepository extends CrudRepository<Appointment, UUID>,
        PagingAndSortingRepository<Appointment, UUID> {

    String ENDPOINT = "appointments";

    @Query(value = """
            SELECT *
            FROM Appointments a
            WHERE a.service_provider = :service_provider
              AND date(a.start_datetime) = date(:date)
    """, nativeQuery = true)
    List<Appointment> findByDateAndServiceProvider(
            @Param("date") @Temporal(TemporalType.DATE) @DateTimeFormat(pattern = "yyyy-MM-dd") Date date,
            @Param("service_provider") UUID serviceProviderId);

}