package com.codein.appointmentservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "appointments",
       uniqueConstraints = @UniqueConstraint(columnNames = {"session_id", "patient_id"}))

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "session_id", nullable = false)
    private UUID sessionId;

    @Column(name = "patient_id", nullable = false)
    private UUID patientId;

    @Column(name = "patient_name", nullable = false)
    private String patientName;

    @Builder.Default
    private LocalDateTime bookedAt = LocalDateTime.now();

    @Builder.Default
    @Enumerated(EnumType.STRING)
    private AppointmentStatus status = AppointmentStatus.BOOKED;

    public enum AppointmentStatus { BOOKED, CANCELLED, STARTED, COMPLETED }

    @Version
    private Long version;
    // getters/setters
}
