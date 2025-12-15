package com.codein.appointmentservice.service;

import com.codein.appointmentservice.dto.AppointmentResponse;
import com.codein.appointmentservice.dto.CreateAppointmentRequest;
import com.codein.appointmentservice.dto.TokenValidationResponse;
import com.codein.appointmentservice.entity.Appointment;
import com.codein.appointmentservice.entity.Session;
import com.codein.appointmentservice.repository.AppointmentRepository;
import com.codein.appointmentservice.repository.SessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final SessionRepository sessionRepository;
    private final RestTemplate restTemplate;

    @Transactional
    public AppointmentResponse createAppointment(CreateAppointmentRequest request, String token) {
        TokenValidationResponse validation = validateToken(token);
        UUID patientId = validation.getUserId();
        
        // Check if patient already has appointment for this session
        if (appointmentRepository.findBySessionIdAndPatientId(request.getSessionId(), patientId).isPresent()) {
            throw new RuntimeException("Patient already has an appointment for this session");
        }
        
        // Get session and check capacity
        Session session = sessionRepository.findById(request.getSessionId())
                .orElseThrow(() -> new RuntimeException("Session not found"));
        
        if (session.getBookedCount() >= session.getCapacity()) {
            throw new RuntimeException("Session is fully booked");
        }
        
        // Get patient name from validation
        String patientName = validation.getName();
        
        // Create appointment
        Appointment appointment = Appointment.builder()
                .sessionId(request.getSessionId())
                .patientId(patientId)
                .patientName(patientName)
                .build();
        
        appointment = appointmentRepository.save(appointment);
        
        // Update session booked count
        session.setBookedCount(session.getBookedCount() + 1);
        sessionRepository.save(session);
        
        return new AppointmentResponse(
                appointment.getId(),
                appointment.getSessionId(),
                appointment.getPatientId(),
                appointment.getBookedAt(),
                appointment.getStatus(),
                appointment.getPatientName(),
                session.getDoctorName()
        );
    }

    public List<AppointmentResponse> getAppointments(String token) {
        TokenValidationResponse validation = validateToken(token);
        boolean isDoctor = "DOCTOR".equals(validation.getRole());
        
        List<Appointment> appointments = isDoctor
            ? appointmentRepository.findByDoctorId(validation.getUserId())
            : appointmentRepository.findAll().stream()
                .filter(appointment -> appointment.getPatientId().equals(validation.getUserId()))
                .toList();
        
        return appointments.stream()
                .map(appointment -> {
                    Session session = sessionRepository.findById(appointment.getSessionId()).orElse(null);
                    return new AppointmentResponse(
                            appointment.getId(),
                            appointment.getSessionId(),
                            appointment.getPatientId(),
                            appointment.getBookedAt(),
                            appointment.getStatus(),
                            isDoctor ? appointment.getPatientName() : null,
                            session != null ? session.getDoctorName() : null
                    );
                })
                .toList();
    }

    private UUID validateTokenAndGetPatientId(String token) {
        TokenValidationResponse validation = validateToken(token);
        return validation.getUserId();
    }
    
    private TokenValidationResponse validateToken(String token) {
        org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        org.springframework.http.HttpEntity<String> entity = new org.springframework.http.HttpEntity<>(headers);
        
        try {
            org.springframework.http.ResponseEntity<TokenValidationResponse> response = restTemplate.exchange(
                    "http://localhost:8080/api/auth/validate-token",
                    org.springframework.http.HttpMethod.POST,
                    entity,
                    TokenValidationResponse.class
            );
            
            TokenValidationResponse validation = response.getBody();
            if (!validation.isValid()) {
                throw new RuntimeException("Invalid token");
            }
            
            return validation;
        } catch (Exception e) {
            throw new RuntimeException("Token validation failed: " + e.getMessage());
        }
    }
}