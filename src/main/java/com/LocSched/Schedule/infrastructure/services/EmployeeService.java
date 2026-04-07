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

    private EmployeeDTO employeeDTO;

    public EmployeeService (EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    @Transactional  
    public Employee createEmployee(Employee employee) {

        try {
            if (employee.getEmail() == null || employee.getEmail().isEmpty()) {
                throw new RuntimeException("Email is required");
            }
            if (employeeRepository.findByEmail(employee.getEmail()).isPresent()) {
                throw new RuntimeException("Employee already exists");
            }
            return employeeRepository.save(employee);
        } catch (Exception e) {
            throw new RuntimeException("Error creating employee", e);
        }
    }

    public Optional<Employee> getEmployeeByEmail(String email) {
        return employeeRepository.findByEmail(email);
    }

    public List<Employee> findAll() {
        return employeeRepository.findAll();
    }

    public Optional<Employee> findById(Long id) {
        return employeeRepository.findById(id);
    }

    @Transactional
    public Employee updateEmployee(Long id, Employee employeeDetails) {
        return employeeRepository.findById(id).map(existingEmployee -> {
            existingEmployee.setName(employeeDetails.getName());
            existingEmployee.setEmail(employeeDetails.getEmail());
            existingEmployee.setPassword(employeeDetails.getPassword());
            return employeeRepository.save(existingEmployee);
        }).orElseThrow(() -> new RuntimeException("Employee not found"));
    }

    @Transactional
    public String deleteEmployee(Long id) {
        employeeRepository.deleteById(id);
        return "Employee deleted successfully";
    }
    
}
