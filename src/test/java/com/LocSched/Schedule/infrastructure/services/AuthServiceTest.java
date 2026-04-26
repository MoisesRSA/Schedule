package com.LocSched.Schedule.infrastructure.services;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.LocSched.Schedule.infrastructure.repository.EmployeeRepository;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private TokenService tokenService;

    @InjectMocks
    private AuthService authService;

    @Test
    void loginWithGoogle_ThrowsException_OnInvalidToken() {
        ReflectionTestUtils.setField(authService, "googleClientId", "test-client-id");

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            authService.loginWithGoogle("fake-invalid-google-token");
        });

        assertTrue(exception.getMessage().contains("Error during Google login"));
    }
}
