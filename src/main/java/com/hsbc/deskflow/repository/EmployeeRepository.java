package com.hsbc.deskflow.repository;

import com.hsbc.deskflow.domain.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    Optional<Employee> findByName(String name);
    Optional<Employee> findByNameIgnoreCase(String name);
    boolean existsByName(String name);
}
