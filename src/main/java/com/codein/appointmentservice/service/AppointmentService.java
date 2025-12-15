package com.codein.appointmentservice.service;

import com.codein.appointmentservice.dto.AppointmentResponse;
import com.codein.appointmentservice.dto.CreateAppointmentRequest;
import com.codein.appointmentservice.dto.TokenValidationResponse;
import com.codein.appointmentservice.entity.Appointment;
import com.codein.appointmentservice.repository.AppointmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final RestTemplate restTemplate;

    public AppointmentResponse createAppointment(CreateAppointmentRequest request, String token) {
        UUID patientId = validateTokenAndGetPatientId(token);
        
        Appointment appointment = Appointment.builder()
                .sessionId(request.getSessionId())
                .patientId(patientId)
                .build();
        
        appointment = appointmentRepository.save(appointment);
        
        return new AppointmentResponse(
                appointment.getId(),
                appointment.getSessionId(),
                appointment.getPatientId(),
                appointment.getBookedAt(),
                appointment.getStatus()
        );
    }

    public List<AppointmentResponse> getPatientAppointments(String token) {
        UUID patientId = validateTokenAndGetPatientId(token);
        
        return appointmentRepository.findAll().stream()
                .filter(appointment -> appointment.getPatientId().equals(patientId))
                .map(appointment -> new AppointmentResponse(
                        appointment.getId(),
                        appointment.getSessionId(),
                        appointment.getPatientId(),
                        appointment.getBookedAt(),
                        appointment.getStatus()
                ))
                .toList();
    }

    private UUID validateTokenAndGetPatientId(String token) {
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
            
            return validation.getUserId();
        } catch (Exception e) {
            throw new RuntimeException("Token validation failed: " + e.getMessage());
        }
    }
}