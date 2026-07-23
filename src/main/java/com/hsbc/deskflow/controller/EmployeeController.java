package com.hsbc.deskflow.controller;

import com.hsbc.deskflow.domain.Employee;
import com.hsbc.deskflow.dto.EmployeeResponse;
import com.hsbc.deskflow.dto.LoginRequest;
import com.hsbc.deskflow.exception.BadRequestException;
import com.hsbc.deskflow.exception.NotFoundException;
import com.hsbc.deskflow.exception.UnauthorizedException;
import com.hsbc.deskflow.repository.EmployeeRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class EmployeeController {

    private final EmployeeRepository employeeRepository;

    public EmployeeController(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    @GetMapping("/employees")
    public EmployeeResponse findByName(@RequestParam String name) {
        String trimmed = name == null ? "" : name.trim();
        if (trimmed.isEmpty()) {
            throw new BadRequestException("name is required");
        }
        return employeeRepository.findByNameIgnoreCase(trimmed)
                .map(e -> new EmployeeResponse(e.getId(), e.getName()))
                .orElseThrow(() -> new NotFoundException("Employee not found: " + trimmed));
    }

    @PostMapping("/login")
    public EmployeeResponse login(@RequestBody LoginRequest request) {
        if (request == null || request.employeeId() == null) {
            throw new BadRequestException("employeeId is required");
        }
        String name = request.employeeName() == null ? "" : request.employeeName().trim();
        if (name.isEmpty()) {
            throw new BadRequestException("employeeName is required");
        }

        Employee employee = employeeRepository.findById(request.employeeId())
                .orElseThrow(() -> new UnauthorizedException("Invalid employee id or name"));

        if (!employee.getName().equalsIgnoreCase(name)) {
            throw new UnauthorizedException("Employee id and name do not match");
        }

        return new EmployeeResponse(employee.getId(), employee.getName());
    }
}
