package com.LocSched.Schedule.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.LocSched.Schedule.DTO.LoginRequestDTO;
import com.LocSched.Schedule.infrastructure.services.AuthService;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "http://localhost:5173")
public class AuthController {
    
    private final AuthService authService;

    public AuthController (AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<String> loginWithGoogle(@RequestBody LoginRequestDTO request) {

        try {   
            String token = authService.loginWithGoogle(request.googleToken());
            return ResponseEntity.ok(token);
        } catch (Exception e){
            return ResponseEntity.status(401).body("Invalid Google Token");
        }
    }
}
