package com.ooad.marketpredictor.web;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.time.Instant;
import java.util.Map;

@RestControllerAdvice
public class ApiExceptionHandler {
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> badRequest(IllegalArgumentException e) { return ResponseEntity.badRequest().body(Map.of("timestamp", Instant.now(), "message", e.getMessage())); }
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<?> unavailable(IllegalStateException e) { return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(Map.of("timestamp", Instant.now(), "message", e.getMessage())); }
}
