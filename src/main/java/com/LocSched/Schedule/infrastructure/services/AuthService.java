package com.LocSched.Schedule.infrastructure.services;

import org.springframework.stereotype.Service;

import com.LocSched.Schedule.infrastructure.entities.Employee;
import com.LocSched.Schedule.infrastructure.repository.EmployeeRepository;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;

import java.util.Collections;

import org.springframework.beans.factory.annotation.Value;

@Service
public class AuthService {
    
    @Value("${google.client.id}")
    private String googleClientId;

    private final EmployeeRepository employeeRepository;
    private final TokenService tokenService;

    public AuthService(EmployeeRepository employeeRepository, TokenService tokenService)     {
        this.employeeRepository = employeeRepository;
        this.tokenService = tokenService;
    }
    
    public String loginWithGoogle(String googleToken) {

        try {    
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new GsonFactory())
            .setAudience(Collections.singletonList(googleClientId))
            .build();
            
            GoogleIdToken idToken = verifier.verify(googleToken);

            if (idToken != null) {
                GoogleIdToken.Payload payload = idToken.getPayload();
                String email = payload.getEmail();
                String name = (String) payload.get("name");
                

                Employee employee = employeeRepository.findByEmail(email)
                .orElseGet(() -> {
                    Employee newEmployee = new Employee();
                    newEmployee.setEmail(email);
                    newEmployee.setName(name);
                    return employeeRepository.save(newEmployee);
                });

                return tokenService.generateToken(employee);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error during Google login", e);
        }

        return null;
    }
    
}
