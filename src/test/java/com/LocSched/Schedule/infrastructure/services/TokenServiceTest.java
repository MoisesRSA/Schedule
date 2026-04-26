package com.LocSched.Schedule.infrastructure.services;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.LocSched.Schedule.infrastructure.entities.Employee;

@ExtendWith(MockitoExtension.class)
public class TokenServiceTest {

    @InjectMocks
    private TokenService tokenService;

    private Employee employee;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(tokenService, "sercretPassword", "test-secret");

        employee = new Employee();
        employee.setEmail("test@example.com");
    }

    @Test
    void generateToken_Success() {
        String token = tokenService.generateToken(employee);

        assertNotNull(token);
        assertFalse(token.isEmpty());
    }

    @Test
    void generateToken_ThrowsException_WhenEmployeeIsNull() {
        assertThrows(NullPointerException.class, () -> {
            tokenService.generateToken(null);
        });
    }

    @Test
    void validateToken_Success() {
        String token = tokenService.generateToken(employee);
        
        String subject = tokenService.validateToken(token);

        assertNotNull(subject);
        assertEquals("test@example.com", subject);
    }

    @Test
    void validateToken_ReturnsNull_WhenTokenInvalid() {
        String subject = tokenService.validateToken("invalid.token.here");

        assertNull(subject);
    }
}
