package com.LocSched.Schedule.infrastructure.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.LocSched.Schedule.DTO.EmployeeDTO;
import com.LocSched.Schedule.infrastructure.entities.Employee;
import com.LocSched.Schedule.infrastructure.repository.EmployeeRepository;

@ExtendWith(MockitoExtension.class)
public class EmployeeServiceTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @InjectMocks
    private EmployeeService employeeService;

    private Employee mockEmployee;

    @BeforeEach
    void setUp() {
        mockEmployee = new Employee();
        mockEmployee.setId(1L);
        mockEmployee.setName("John Doe");
        mockEmployee.setEmail("john@example.com");
        mockEmployee.setPhotoUrl("http://example.com/photo.jpg");
        mockEmployee.setStatus("ACTIVE");
    }

    @Test
    void createEmployee_Success() {
        when(employeeRepository.findByEmail(mockEmployee.getEmail())).thenReturn(Optional.empty());
        when(employeeRepository.save(any(Employee.class))).thenReturn(mockEmployee);

        EmployeeDTO result = employeeService.createEmployee(mockEmployee);

        assertNotNull(result);
        assertEquals("John Doe", result.name());
        verify(employeeRepository, times(1)).save(mockEmployee);
    }

    @Test
    void createEmployee_ThrowsExceptionWhenEmailEmpty() {
        mockEmployee.setEmail(null);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            employeeService.createEmployee(mockEmployee);
        });

        assertTrue(exception.getMessage().contains("Email is required") || 
                   (exception.getCause() != null && exception.getCause().getMessage().contains("Email is required")));
        verify(employeeRepository, never()).save(any(Employee.class));
    }

    @Test
    void createEmployee_ThrowsExceptionWhenEmployeeExists() {
        when(employeeRepository.findByEmail(mockEmployee.getEmail())).thenReturn(Optional.of(mockEmployee));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            employeeService.createEmployee(mockEmployee);
        });

        assertTrue(exception.getMessage().contains("Employee already exists") || 
                   (exception.getCause() != null && exception.getCause().getMessage().contains("Employee already exists")));
        verify(employeeRepository, never()).save(any(Employee.class));
    }

    @Test
    void findAll_Success() {
        when(employeeRepository.findAll()).thenReturn(List.of(mockEmployee));

        List<EmployeeDTO> results = employeeService.findAll();

        assertFalse(results.isEmpty());
        assertEquals(1, results.size());
        assertEquals("John Doe", results.get(0).name());
    }

    @Test
    void findById_Success() {
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(mockEmployee));

        Optional<EmployeeDTO> result = employeeService.findById(1L);

        assertTrue(result.isPresent());
        assertEquals("John Doe", result.get().name());
    }

    @Test
    void findById_NotFound() {
        when(employeeRepository.findById(1L)).thenReturn(Optional.empty());

        Optional<EmployeeDTO> result = employeeService.findById(1L);

        assertTrue(result.isEmpty());
    }

    @Test
    void updateEmployee_Success() {
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(mockEmployee));
        when(employeeRepository.save(any(Employee.class))).thenReturn(mockEmployee);

        Employee updatedDetails = new Employee();
        updatedDetails.setName("Jane Doe");
        updatedDetails.setStatus("INACTIVE");

        EmployeeDTO result = employeeService.updateEmployee(1L, updatedDetails);

        assertNotNull(result);
        verify(employeeRepository, times(1)).save(mockEmployee);
    }

    @Test
    void updateEmployee_ThrowsExceptionWhenNotFound() {
        when(employeeRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            employeeService.updateEmployee(1L, mockEmployee);
        });

        verify(employeeRepository, never()).save(any(Employee.class));
    }

    @Test
    void deleteEmployee_Success() {
        String result = employeeService.deleteEmployee(1L);

        assertEquals("Employee deleted successfully", result);
        verify(employeeRepository, times(1)).deleteById(1L);
    }
}
