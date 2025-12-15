package com.codein.appointmentservice.controller;

import com.codein.appointmentservice.dto.*;
import com.codein.appointmentservice.service.SessionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/appointmentservice/api/sessions")
@RequiredArgsConstructor
public class SessionController {

    private final SessionService sessionService;

    @PostMapping
    public ResponseEntity<SessionResponse> createSession(
            @RequestBody CreateSessionRequest request,
            @RequestHeader("Authorization") String authHeader) {
        log.info("Creating session for doctor: {}", request.getDoctorId());
        String token = authHeader.replace("Bearer ", "");
        SessionResponse response = sessionService.createSession(request, token);
        log.info("Session created with ID: {}", response.id());
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<SessionResponse>> getDoctorSessions(
            @RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        List<SessionResponse> sessions = sessionService.getDoctorSessions(token);
        return ResponseEntity.ok(sessions);
    }

    @GetMapping("/available/{doctorId}")
    public ResponseEntity<List<SessionResponse>> getAvailableSessions(
            @PathVariable UUID doctorId,
            @RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        List<SessionResponse> sessions = sessionService.getAvailableSessions(doctorId, token);
        return ResponseEntity.ok(sessions);
    }
}