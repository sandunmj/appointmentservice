package com.codein.appointmentservice.service;

import com.codein.appointmentservice.dto.*;
import com.codein.appointmentservice.entity.Session;
import com.codein.appointmentservice.repository.SessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SessionService {

    private final SessionRepository sessionRepository;
    private final RestTemplate restTemplate;

    public SessionResponse createSession(CreateSessionRequest request, String token) {
        TokenValidationResponse validation = validateToken(token);
        
        if (!validation.isValid() || !"DOCTOR".equals(validation.getRole())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid token or insufficient permissions");
        }

        // Validate mandatory fields
        if (request.getDoctorId() == null || request.getSessionStartTime() == null || 
            request.getSessionEndTime() == null || request.getMaxAppointmentCount() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "All fields are mandatory: doctorId, sessionStartTime, sessionEndTime, maxAppointmentCount");
        }

        // Validate doctor ID matches authenticated user
        if (!request.getDoctorId().equals(validation.getUserId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Doctor ID mismatch with authenticated user");
        }

        if (request.getSessionStartTime().isAfter(request.getSessionEndTime())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Start time must be before end time");
        }

        Session session = Session.builder()
                .doctorId(request.getDoctorId())
                .doctorName(validation.getName())
                .date(request.getSessionStartTime().toLocalDate())
                .startTime(request.getSessionStartTime().toLocalTime())
                .endTime(request.getSessionEndTime().toLocalTime())
                .capacity(request.getMaxAppointmentCount())
                .status(Session.SessionStatus.SCHEDULED)
                .build();

        Session saved = sessionRepository.save(session);

        return new SessionResponse(
                saved.getId(),
                saved.getDoctorId(),
                saved.getDoctorName(),
                saved.getDate(),
                saved.getStartTime(),
                saved.getEndTime(),
                saved.getStatus(),
                saved.getCapacity(),
                saved.getBookedCount()
        );
    }

    private TokenValidationResponse validateToken(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<TokenValidationResponse> response = restTemplate.exchange(
                    "http://localhost:8080/api/auth/validate-token",
                    HttpMethod.POST,
                    entity,
                    TokenValidationResponse.class
            );
            return response.getBody();
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token validation failed");
        }
    }



    public List<SessionResponse> getDoctorSessions(String token) {
        TokenValidationResponse validation = validateToken(token);
        
        if (!validation.isValid() || !"DOCTOR".equals(validation.getRole())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid token or insufficient permissions");
        }

        return sessionRepository.findByDoctorId(validation.getUserId())
                .stream()
                .map(s -> new SessionResponse(
                        s.getId(),
                        s.getDoctorId(),
                        s.getDoctorName(),
                        s.getDate(),
                        s.getStartTime(),
                        s.getEndTime(),
                        s.getStatus(),
                        s.getCapacity(),
                        s.getBookedCount()))
                .toList();
    }

    public List<SessionResponse> getAvailableSessions(UUID doctorId, String token) {
        TokenValidationResponse validation = validateToken(token);
        
        if (!validation.isValid()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid token");
        }

        return sessionRepository.findByDoctorId(doctorId)
                .stream()
                .filter(s -> s.getStatus() == Session.SessionStatus.SCHEDULED || s.getStatus() == Session.SessionStatus.STARTED)
                .filter(s -> s.getBookedCount() < s.getCapacity())
                .map(s -> new SessionResponse(
                        s.getId(),
                        s.getDoctorId(),
                        s.getDoctorName(),
                        s.getDate(),
                        s.getStartTime(),
                        s.getEndTime(),
                        s.getStatus(),
                        s.getCapacity(),
                        s.getBookedCount()))
                .toList();
    }
    

}

