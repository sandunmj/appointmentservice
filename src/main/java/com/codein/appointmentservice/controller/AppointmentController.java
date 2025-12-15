package com.codein.appointmentservice.controller;

import com.codein.appointmentservice.dto.*;
import com.codein.appointmentservice.service.AppointmentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/appointmentservice/api/appointments")
@RequiredArgsConstructor
public class AppointmentController {

    private final AppointmentService appointmentService;

    @PostMapping
    public ResponseEntity<AppointmentResponse> createAppointment(
            @RequestBody CreateAppointmentRequest request,
            @RequestHeader("Authorization") String authHeader) {
        log.info("Creating appointment for session: {}", request.getSessionId());
        String token = authHeader.replace("Bearer ", "");
        AppointmentResponse response = appointmentService.createAppointment(request, token);
        log.info("Appointment created with ID: {}", response.id());
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<AppointmentResponse>> getAppointments(
            @RequestHeader("Authorization") String authHeader) {
        log.info("Fetching appointments");
        String token = authHeader.replace("Bearer ", "");
        List<AppointmentResponse> appointments = appointmentService.getAppointments(token);
        log.info("Found {} appointments", appointments.size());
        return ResponseEntity.ok(appointments);
    }
}