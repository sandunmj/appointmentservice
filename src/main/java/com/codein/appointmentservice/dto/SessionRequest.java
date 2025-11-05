package com.codein.appointmentservice.dto;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

public record SessionRequest(
        UUID doctorId,
        LocalDate date,
        LocalTime startTime,
        LocalTime endTime,
        Integer capacity
) {}

