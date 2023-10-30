package com.github.flombois.events;

import com.github.flombois.entities.Appointment;
import com.github.flombois.repositories.AppointmentRepository;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.rest.core.annotation.HandleBeforeCreate;
import org.springframework.data.rest.core.annotation.HandleBeforeSave;
import org.springframework.data.rest.core.annotation.RepositoryEventHandler;

import java.time.Duration;
import java.util.Date;
import java.util.Set;

@RepositoryEventHandler
public class AppointmentEventHandler {

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private Validator validator;

    /**
     * Validate appointment because RepositoryEventHandler is fired before validation
     * @param appointment To be validated
     * @throws ConstraintViolationException if validation fails
     */
    private void validate(Appointment appointment) {
        Set<ConstraintViolation<Appointment>> constraintViolations = validator.validate(appointment);
        if(!constraintViolations.isEmpty()) {
            throw new ConstraintViolationException("Appointment validation has failed", constraintViolations);
        }
    }

    @HandleBeforeCreate
    @HandleBeforeSave
    public void handleAppointmentSave(Appointment appointment) {
        validate(appointment);
       boolean isValid =  appointmentRepository.findAppointmentsByStartDateTimeAndServiceProvider(
               Date.from(appointment.getStartDateTime().toInstant()), appointment.getServiceProvider().getId())
               .stream().filter(existing -> isOverlapping(existing, appointment))
               .toList().isEmpty();
       if(!isValid) {
           throw new DataIntegrityViolationException("An appointment already exists for specified dates");
       }
    }

    /**
     * Check if the candidate appointment schedule overlap an existing appointment schedule
     * @param existing An existing appointment
     * @param candidate A candidate appointment
     * @return true if the existing appointment either does not end before the candidate appointment starts or does not
     * start after the candidate ends, false otherwise
     */
    private boolean isOverlapping(Appointment existing, Appointment candidate) {
        return !(endsBefore(existing, candidate) || startsAfter(existing, candidate));
    }

    /**
     * Check if the existing appointment ends before the candidate appointment starts
     * @param existing An existing appointment
     * @param candidate A candidate appointment
     * @return true if the existing appointment ends before the candidate appointment starts, false otherwise
     */
    private boolean endsBefore(Appointment existing, Appointment candidate) {
        return existing.getStartDateTime()
                .plus(Duration.ofMinutes(existing.getDuration()))
                .minus(Duration.ofSeconds(1)) // Remove one second to avoid edge case during comparison
                .isBefore(candidate.getStartDateTime());
    }

    /**
     * Check if the existing appointment starts after the candidate appointment ends
     * @param existing An existing appointment
     * @param candidate A candidate appointment
     * @return true if the existing appointment starts after the candidate appointment ends, false otherwise
     */
    private boolean startsAfter(Appointment existing, Appointment candidate) {
        return existing.getStartDateTime().isAfter(candidate.getStartDateTime()
                .plus(Duration.ofMinutes(existing.getDuration())));
    }
}
