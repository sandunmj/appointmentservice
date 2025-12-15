package com.codein.appointmentservice.dto;

import java.util.UUID;

public class CreatePrescriptionRequest {
    private UUID appointmentId;
    private String notes;
    private Object medications;

    public UUID getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(UUID appointmentId) {
        this.appointmentId = appointmentId;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Object getMedications() {
        return medications;
    }

    public void setMedications(Object medications) {
        this.medications = medications;
    }
}