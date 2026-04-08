package com.LocSched.Schedule.infrastructure.services;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.LocSched.Schedule.DTO.EmployeeDTO;
import com.LocSched.Schedule.infrastructure.entities.Employee;
import com.LocSched.Schedule.infrastructure.repository.EmployeeRepository;

import jakarta.transaction.Transactional;

@Service
public class EmployeeService {

    private final EmployeeRepository employeeRepository;

    public EmployeeService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    public EmployeeDTO toDTO(Employee employee) {
        return new EmployeeDTO(
            employee.getId(),
            employee.getName(),
            employee.getPhotoUrl(),
            employee.getStatus()
        );
    }

    @Transactional
    public EmployeeDTO createEmployee(Employee employee) {
        try {
            if (employee.getEmail() == null || employee.getEmail().isEmpty()) {
                throw new RuntimeException("Email is required");
            }
            if (employeeRepository.findByEmail(employee.getEmail()).isPresent()) {
                throw new RuntimeException("Employee already exists");
            }
            return toDTO(employeeRepository.save(employee));
        } catch (Exception e) {
            throw new RuntimeException("Error creating employee", e);
        }
    }

    public Optional<Employee> getEmployeeByEmail(String email) {
        return employeeRepository.findByEmail(email);
    }

    public List<EmployeeDTO> findAll() {
        return employeeRepository.findAll()
                .stream()
                .map(this::toDTO)
                .toList();
    }

    public Optional<EmployeeDTO> findById(Long id) {
        return employeeRepository.findById(id).map(this::toDTO);
    }

    @Transactional
    public EmployeeDTO updateEmployee(Long id, Employee employeeDetails) {
        return employeeRepository.findById(id).map(existingEmployee -> {
            existingEmployee.setName(employeeDetails.getName());
            existingEmployee.setPhotoUrl(employeeDetails.getPhotoUrl());
            existingEmployee.setStatus(employeeDetails.getStatus());
            return toDTO(employeeRepository.save(existingEmployee));
        }).orElseThrow(() -> new RuntimeException("Employee not found"));
    }

    @Transactional
    public String deleteEmployee(Long id) {
        employeeRepository.deleteById(id);
        return "Employee deleted successfully";
    }

}
