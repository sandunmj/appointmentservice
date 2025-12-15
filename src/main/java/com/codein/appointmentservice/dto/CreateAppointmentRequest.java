package com.codein.appointmentservice.dto;

import java.util.UUID;

public class CreateAppointmentRequest {
    private UUID sessionId;
    private UUID patientId;

    public UUID getSessionId() {
        return sessionId;
    }

    public void setSessionId(UUID sessionId) {
        this.sessionId = sessionId;
    }

    public UUID getPatientId() {
        return patientId;
    }

    public void setPatientId(UUID patientId) {
        this.patientId = patientId;
    }
}