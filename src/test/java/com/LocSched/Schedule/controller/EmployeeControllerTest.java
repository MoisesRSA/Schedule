package com.LocSched.Schedule.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.LocSched.Schedule.DTO.EmployeeDTO;
import com.LocSched.Schedule.infrastructure.entities.Employee;
import com.LocSched.Schedule.infrastructure.services.EmployeeService;
import com.fasterxml.jackson.databind.ObjectMapper;

@ExtendWith(MockitoExtension.class)
public class EmployeeControllerTest {

    private MockMvc mockMvc;

    @Mock
    private EmployeeService employeeService;

    @InjectMocks
    private EmployeeController employeeController;

    private ObjectMapper objectMapper;
    private Employee mockEmployee;
    private EmployeeDTO mockEmployeeDTO;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(employeeController).build();
        objectMapper = new ObjectMapper();

        mockEmployee = new Employee();
        mockEmployee.setId(1L);
        mockEmployee.setName("Jane Doe");
        mockEmployee.setEmail("jane@example.com");

        mockEmployeeDTO = new EmployeeDTO(1L, "Jane Doe", null, "ACTIVE");
    }

    @Test
    void createEmployee_ReturnsOk() throws Exception {
        when(employeeService.createEmployee(any(Employee.class))).thenReturn(mockEmployeeDTO);

        mockMvc.perform(post("/employee/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(mockEmployee)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Jane Doe"));
    }

    @Test
    void findAll_ReturnsOk() throws Exception {
        when(employeeService.findAll()).thenReturn(List.of(mockEmployeeDTO));

        mockMvc.perform(get("/employee/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Jane Doe"));
    }

    @Test
    void findById_ReturnsOk_WhenExists() throws Exception {
        when(employeeService.findById(1L)).thenReturn(Optional.of(mockEmployeeDTO));

        mockMvc.perform(get("/employee/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Jane Doe"));
    }

    @Test
    void findById_ReturnsNotFound_WhenDoesNotExist() throws Exception {
        when(employeeService.findById(1L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/employee/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateEmployee_ReturnsOk() throws Exception {
        when(employeeService.updateEmployee(eq(1L), any(Employee.class))).thenReturn(mockEmployeeDTO);

        mockMvc.perform(put("/employee/update/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(mockEmployee)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Jane Doe"));
    }

    @Test
    void deleteEmployee_ReturnsOk() throws Exception {
        when(employeeService.deleteEmployee(1L)).thenReturn("Employee deleted successfully");

        mockMvc.perform(delete("/employee/delete/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Employee deleted successfully"));
    }
}
