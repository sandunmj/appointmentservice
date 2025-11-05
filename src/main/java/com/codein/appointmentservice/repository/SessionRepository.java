package com.codein.appointmentservice.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import com.codein.appointmentservice.entity.Session;

public interface SessionRepository extends JpaRepository<Session, UUID> {
    List<Session> findByDoctorId(UUID doctorId);
}