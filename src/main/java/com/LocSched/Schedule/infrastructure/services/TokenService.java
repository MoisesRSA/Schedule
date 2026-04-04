package com.LocSched.Schedule.infrastructure.services;

import org.springframework.stereotype.Service;

import com.LocSched.Schedule.infrastructure.entities.Employee;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;


import org.springframework.beans.factory.annotation.Value;

@Service
public class TokenService {
    
    @Value("${api.security.token.secret}")
    private String sercretPassword;

    public String generateToken (Employee employee) {
        try {

            Algorithm algorithm = Algorithm.HMAC256(sercretPassword);
            return JWT.create()
            .withIssuer("LocSched")
            .withSubject(employee.getEmail())
            .withExpiresAt(getExpirationDate())
            .sign(algorithm);

        } catch (JWTCreationException e) {
            throw new RuntimeException("Error generating token", e);
        }
    }

    public String validateToken(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(sercretPassword);
            return JWT.require(algorithm)
            .withIssuer("LocSched")
            .build()
            .verify(token)
            .getSubject();
        } catch (JWTVerificationException e) {
            return null;
        }
    }

    private Instant getExpirationDate() {
        return LocalDateTime.now().plusHours(2).toInstant(ZoneOffset.of("-03:00"));
    }
}
