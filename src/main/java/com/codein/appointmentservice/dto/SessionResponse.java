package com.codein.appointmentservice.dto;

import com.codein.appointmentservice.entity.Session.SessionStatus;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

public record SessionResponse(
        UUID id,
        UUID doctorId,
        LocalDate date,
        LocalTime startTime,
        LocalTime endTime,
        SessionStatus status,
        Integer capacity,
        Integer bookedCount
) {}
