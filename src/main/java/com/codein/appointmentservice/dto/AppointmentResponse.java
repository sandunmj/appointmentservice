package com.codein.appointmentservice.dto;

import com.codein.appointmentservice.entity.Appointment;
import java.time.LocalDateTime;
import java.util.UUID;

public record AppointmentResponse(
    UUID id,
    UUID sessionId,
    UUID patientId,
    LocalDateTime bookedAt,
    Appointment.AppointmentStatus status,
    String patientName,
    String doctorName
) {}