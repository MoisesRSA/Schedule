package com.LocSched.Schedule.infrastructure.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.LocSched.Schedule.infrastructure.entities.Employee;
import com.google.common.base.Optional;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    
    Optional<Employee> findByEmail(String email);
}
