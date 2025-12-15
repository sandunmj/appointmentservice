package com.codein.appointmentservice.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public class CreateSessionRequest {
    private UUID doctorId;
    private LocalDateTime sessionStartTime;
    private LocalDateTime sessionEndTime;
    private Integer maxAppointmentCount;

    public UUID getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(UUID doctorId) {
        this.doctorId = doctorId;
    }

    public LocalDateTime getSessionStartTime() {
        return sessionStartTime;
    }

    public void setSessionStartTime(LocalDateTime sessionStartTime) {
        this.sessionStartTime = sessionStartTime;
    }

    public LocalDateTime getSessionEndTime() {
        return sessionEndTime;
    }

    public void setSessionEndTime(LocalDateTime sessionEndTime) {
        this.sessionEndTime = sessionEndTime;
    }

    public Integer getMaxAppointmentCount() {
        return maxAppointmentCount;
    }

    public void setMaxAppointmentCount(Integer maxAppointmentCount) {
        this.maxAppointmentCount = maxAppointmentCount;
    }
}