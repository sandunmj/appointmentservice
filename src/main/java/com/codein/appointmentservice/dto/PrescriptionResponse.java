package com.codein.appointmentservice.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record PrescriptionResponse(
    UUID id,
    UUID appointmentId,
    String notes,
    String medications,
    LocalDateTime createdAt
) {}