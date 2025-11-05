package com.codein.appointmentservice.service;

import com.codein.appointmentservice.dto.SessionRequest;
import com.codein.appointmentservice.dto.SessionResponse;
import com.codein.appointmentservice.entity.Session;
import com.codein.appointmentservice.repository.SessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SessionService {

    private final SessionRepository sessionRepository;

    public SessionResponse scheduleSession(SessionRequest request) {
        if (request.startTime().isAfter(request.endTime())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Start time must be before end time");
        }

        Session session = Session.builder()
                .doctorId(request.doctorId())
                .date(request.date())
                .startTime(request.startTime())
                .endTime(request.endTime())
                .capacity(request.capacity() != null ? request.capacity() : 1)
                .status(Session.SessionStatus.SCHEDULED)
                .build();

        Session saved = sessionRepository.save(session);

        return new SessionResponse(
                saved.getId(),
                saved.getDoctorId(),
                saved.getDate(),
                saved.getStartTime(),
                saved.getEndTime(),
                saved.getStatus(),
                saved.getCapacity(),
                saved.getBookedCount()
        );
    }

    public List<SessionResponse> getDoctorSessions(UUID doctorId) {
        return sessionRepository.findByDoctorId(doctorId)
                .stream()
                .map(s -> new SessionResponse(
                        s.getId(),
                        s.getDoctorId(),
                        s.getDate(),
                        s.getStartTime(),
                        s.getEndTime(),
                        s.getStatus(),
                        s.getCapacity(),
                        s.getBookedCount()))
                .toList();
    }
}

