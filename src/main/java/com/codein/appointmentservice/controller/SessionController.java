package com.codein.appointmentservice.controller;

import com.codein.appointmentservice.dto.SessionRequest;
import com.codein.appointmentservice.dto.SessionResponse;
import com.codein.appointmentservice.service.SessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/sessions")
@RequiredArgsConstructor
public class SessionController {

    private final SessionService sessionService;

    // Doctor schedules availability session
    @PostMapping("/schedule")
    public ResponseEntity<SessionResponse> scheduleSession(@RequestBody SessionRequest request) {
        SessionResponse response = sessionService.scheduleSession(request);
        return ResponseEntity.ok(response);
    }

    // Get doctor’s sessions for today
    @GetMapping("/doctor/{doctorId}")
    public ResponseEntity<List<SessionResponse>> getDoctorSessions(@PathVariable UUID doctorId) {
        return ResponseEntity.ok(sessionService.getDoctorSessions(doctorId));
    }
}
