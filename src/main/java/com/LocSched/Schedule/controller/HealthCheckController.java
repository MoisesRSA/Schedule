package com.LocSched.Schedule.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class HealthCheckController {

    @Value("${app.health.secret-key}")
    private String secretKey;

    @GetMapping("/api/public/health")
    public ResponseEntity<?> healthCheck(
            @RequestParam(value = "key", required = false) String paramKey,
            @RequestHeader(value = "X-Health-Key", required = false) String headerKey) {

        if ((paramKey != null && paramKey.equals(secretKey)) || 
            (headerKey != null && headerKey.equals(secretKey))) {
            return ResponseEntity.ok(Map.of("status", "UP"));
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Unauthorized access"));
    }
}
