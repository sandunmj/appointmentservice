package com.codein.appointmentservice.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, String>> handleRuntimeException(RuntimeException ex) {
        String message = ex.getMessage();
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        
        if (message.contains("already has an appointment")) {
            status = HttpStatus.CONFLICT;
        } else if (message.contains("fully booked")) {
            status = HttpStatus.BAD_REQUEST;
        } else if (message.contains("not found")) {
            status = HttpStatus.NOT_FOUND;
        } else if (message.contains("Invalid token") || message.contains("Token validation failed")) {
            status = HttpStatus.UNAUTHORIZED;
        }
        
        return ResponseEntity.status(status)
                .body(Map.of("error", message));
    }
}