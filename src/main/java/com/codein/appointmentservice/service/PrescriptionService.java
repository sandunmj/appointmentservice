package com.codein.appointmentservice.service;

import com.codein.appointmentservice.dto.CreatePrescriptionRequest;
import com.codein.appointmentservice.dto.PrescriptionResponse;
import com.codein.appointmentservice.dto.TokenValidationResponse;
import com.codein.appointmentservice.entity.Appointment;
import com.codein.appointmentservice.entity.Prescription;
import com.codein.appointmentservice.entity.Session;
import com.codein.appointmentservice.repository.AppointmentRepository;
import com.codein.appointmentservice.repository.PrescriptionRepository;
import com.codein.appointmentservice.repository.SessionRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PrescriptionService {

    private final PrescriptionRepository prescriptionRepository;
    private final AppointmentRepository appointmentRepository;
    private final SessionRepository sessionRepository;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    
    @Value("${auth.service.url}")
    private String authServiceUrl;

    public PrescriptionResponse createPrescription(CreatePrescriptionRequest request, String token) {
        TokenValidationResponse validation = validateToken(token);
        
        if (!"DOCTOR".equals(validation.getRole())) {
            throw new RuntimeException("Only doctors can create prescriptions");
        }

        Appointment appointment = appointmentRepository.findById(request.getAppointmentId())
                .orElseThrow(() -> new RuntimeException("Appointment not found"));

        Session session = sessionRepository.findById(appointment.getSessionId())
                .orElseThrow(() -> new RuntimeException("Session not found"));

        if (!session.getDoctorId().equals(validation.getUserId())) {
            throw new RuntimeException("Doctor can only create prescriptions for their own appointments");
        }

        String medicationsJson;
        try {
            medicationsJson = objectMapper.writeValueAsString(request.getMedications());
        } catch (Exception e) {
            throw new RuntimeException("Invalid medications format");
        }

        Prescription prescription = Prescription.builder()
                .appointmentId(request.getAppointmentId())
                .patientId(appointment.getPatientId())
                .notes(request.getNotes())
                .medications(medicationsJson)
                .build();

        prescription = prescriptionRepository.save(prescription);

        return new PrescriptionResponse(
                prescription.getId(),
                prescription.getAppointmentId(),
                prescription.getNotes(),
                prescription.getMedications(),
                prescription.getCreatedAt()
        );
    }

    private TokenValidationResponse validateToken(String token) {
        org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        org.springframework.http.HttpEntity<String> entity = new org.springframework.http.HttpEntity<>(headers);
        
        try {
            org.springframework.http.ResponseEntity<TokenValidationResponse> response = restTemplate.exchange(
                    authServiceUrl,
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

    public List<PrescriptionResponse> getPrescriptionsByPatientId(UUID patientId, String token) {
        TokenValidationResponse validation = validateToken(token);
        
        if (!"DOCTOR".equals(validation.getRole()) && !"PATIENT".equals(validation.getRole())) {
            throw new RuntimeException("Only doctors and patients can view prescriptions");
        }

        if ("PATIENT".equals(validation.getRole()) && !validation.getUserId().equals(patientId)) {
            throw new RuntimeException("Patients can only view their own prescriptions");
        }

        List<Prescription> prescriptions = prescriptionRepository.findByPatientId(patientId);
        
        return prescriptions.stream()
                .map(p -> new PrescriptionResponse(
                        p.getId(),
                        p.getAppointmentId(),
                        p.getNotes(),
                        p.getMedications(),
                        p.getCreatedAt()
                ))
                .toList();
    }
}