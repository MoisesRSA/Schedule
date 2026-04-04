package com.LocSched.Schedule.infrastructure.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.LocSched.Schedule.infrastructure.entities.Employee;
import java.util.Optional;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    
    Optional<Employee> findByEmail(String email);
}
