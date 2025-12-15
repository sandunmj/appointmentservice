package com.codein.appointmentservice.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.codein.appointmentservice.entity.Appointment;

public interface AppointmentRepository extends JpaRepository<Appointment, UUID> {
    Optional<Appointment> findBySessionIdAndPatientId(UUID sessionId, UUID patientId);
    
    @Query("SELECT a FROM Appointment a JOIN Session s ON a.sessionId = s.id WHERE s.doctorId = :doctorId")
    List<Appointment> findByDoctorId(@Param("doctorId") UUID doctorId);
    
    @Query("SELECT a FROM Appointment a JOIN Session s ON a.sessionId = s.id WHERE s.doctorId = :doctorId AND a.status = :status")
    List<Appointment> findByDoctorIdAndStatus(@Param("doctorId") UUID doctorId, @Param("status") Appointment.AppointmentStatus status);
}