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
               .stream().filter(appointment::overlaps)
               .toList().isEmpty();
       if(!isValid) {
           throw new DataIntegrityViolationException("An appointment already exists for specified dates");
       }
    }

}
