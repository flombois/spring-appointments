package com.github.flombois.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "appointments", schema = "public")
public class Appointment extends PersistentEntity<UUID> {

    @NotNull
    @ManyToOne
    @JoinColumn(name = "customer", nullable = false)
    private User customer;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "service_provider", nullable = false)
    private ServiceProvider serviceProvider;

    @NotNull
    @Column(name = "start_datetime", columnDefinition = "TIMESTAMP WITH TIME ZONE", nullable = false)
    private OffsetDateTime startDateTime;

    // Minimum duration of an appointment is 5 minutes
    @Min(5)
    @Column(name = "duration", nullable = false)
    private short duration;


    /**
     * Calculate appointment end
     * @return The appointment end timestamp
     */
    @JsonIgnore
    @Transient
    public OffsetDateTime getEndDateTime() {
        // end = start + duration - 1s
        return startDateTime.plus(Duration.ofMinutes(duration)).minus(Duration.ofSeconds(1));
    }

    /**
     * Check if the appointment is over before the supplied appointment starts
     * @param otherAppointment The appointment to compare
     * @return true if appointment is over, false otherwise
     */
    @JsonIgnore
    @Transient
    public boolean finishBefore(Appointment otherAppointment) {
        return getEndDateTime().isBefore(otherAppointment.getStartDateTime());
    }

    /**
     * Check if the appointment starts after the supplied appointment is over
     * @param otherAppointment The appointment to compare
     * @return true if appointment starts after, false otherwise
     */
    @JsonIgnore
    @Transient
    public boolean startAfter(Appointment otherAppointment) {
        return getStartDateTime().isAfter(otherAppointment.getEndDateTime());
    }

    /**
     * Compare appointments to check overlapping
     * @param otherAppointment The appointment to compare
     * @return false if the appointment is either finished or starts after the supplied appointment
     */
    @JsonIgnore
    @Transient
    public boolean overlaps(Appointment otherAppointment) {
        return !(finishBefore(otherAppointment) || startAfter(otherAppointment));
    }


    public User getCustomer() {
        return customer;
    }

    public void setCustomer(User customer) {
        this.customer = customer;
    }

    public ServiceProvider getServiceProvider() {
        return serviceProvider;
    }

    public void setServiceProvider(ServiceProvider serviceProvider) {
        this.serviceProvider = serviceProvider;
    }

    public OffsetDateTime getStartDateTime() {
        return startDateTime;
    }

    public void setStartDateTime(OffsetDateTime startDateTime) {
        this.startDateTime = startDateTime;
    }

    public short getDuration() {
        return duration;
    }

    public void setDuration(short duration) {
        this.duration = duration;
    }
}
