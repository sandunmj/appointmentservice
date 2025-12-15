package com.codein.appointmentservice.controller;

import com.codein.appointmentservice.dto.CreatePrescriptionRequest;
import com.codein.appointmentservice.dto.PrescriptionResponse;
import com.codein.appointmentservice.service.PrescriptionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/appointmentservice/api/prescriptions")
@RequiredArgsConstructor
public class PrescriptionController {

    private final PrescriptionService prescriptionService;

    @PostMapping
    public ResponseEntity<PrescriptionResponse> createPrescription(
            @RequestBody CreatePrescriptionRequest request,
            @RequestHeader("Authorization") String authHeader) {
        log.info("Creating prescription for appointment: {}", request.getAppointmentId());
        String token = authHeader.replace("Bearer ", "");
        PrescriptionResponse response = prescriptionService.createPrescription(request, token);
        log.info("Prescription created with ID: {}", response.id());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<PrescriptionResponse>> getPrescriptionsByPatientId(
            @PathVariable UUID patientId,
            @RequestHeader("Authorization") String authHeader) {
        log.info("Getting prescriptions for patient: {}", patientId);
        String token = authHeader.replace("Bearer ", "");
        List<PrescriptionResponse> prescriptions = prescriptionService.getPrescriptionsByPatientId(patientId, token);
        log.info("Found {} prescriptions for patient: {}", prescriptions.size(), patientId);
        return ResponseEntity.ok(prescriptions);
    }
}