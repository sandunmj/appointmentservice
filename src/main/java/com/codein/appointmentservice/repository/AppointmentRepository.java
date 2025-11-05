package com.codein.appointmentservice.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.codein.appointmentservice.entity.Appointment;

public interface AppointmentRepository extends JpaRepository<Appointment, UUID> {
    Optional<Appointment> findBySessionIdAndPatientId(UUID sessionId, UUID patientId);
}