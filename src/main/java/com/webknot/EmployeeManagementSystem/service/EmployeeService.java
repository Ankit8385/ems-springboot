package com.webknot.EmployeeManagementSystem.service;

import com.webknot.EmployeeManagementSystem.model.Employee;
import com.webknot.EmployeeManagementSystem.exception.ResourceNotFoundException;
import com.webknot.EmployeeManagementSystem.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.Instant;
import java.util.List;

@Service
//@RequiredArgsConstructor
public class EmployeeService {
    private static final Logger log = LoggerFactory.getLogger(EmployeeService.class);

    @Autowired
    private EmployeeRepository employeeRepository;

    @Cacheable(value = "employees", key = "#id")
    public Employee getEmployeeById(Long id) {
        Instant start = Instant.now();
        log.info("Fetching employee with id: {}", id != null ? id : "NULL");

        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + id));

        Instant end = Instant.now();
        log.info("Time taken to fetch employee with id {}: {} ms", id, end.toEpochMilli() - start.toEpochMilli());

        return employee;
    }

    @Cacheable(value = "employees")
    public List<Employee> getAllEmployees() {
        Instant start = Instant.now();
        log.info("Fetching all employees");

        List<Employee> employees = employeeRepository.findAll();

        Instant end = Instant.now();
        log.info("Time taken to fetch all employees: {} ms", end.toEpochMilli() - start.toEpochMilli());

        return employees;
    }

    @Transactional
    @CacheEvict(value = "employees", allEntries = true)
    public Employee createEmployee(Employee employee) {
        log.info("Creating new employee: {}", employee);
        return employeeRepository.save(employee);
    }

    @Transactional
    @CacheEvict(value = "employees", key = "#id")
    public Employee updateEmployee(Long id, Employee employeeDetails) {
        log.info("Updating employee with id: {}", id);

        Employee employee = getEmployeeById(id);

        if (employeeDetails == null) {
            log.error("updateEmployee: Received null employeeDetails for id {}", id);
            throw new IllegalArgumentException("Employee details cannot be null");
        }

        // Updating only non-null fields
//        if (employeeDetails.getFirstName() != null) {
//            employee.setFirstName(employeeDetails.getFirstName());
//        }
//        if (employeeDetails.getLastName() != null) {
//            employee.setLastName(employeeDetails.getLastName());
//        }
//        if (employeeDetails.getEmail() != null) {
//            employee.setEmail(employeeDetails.getEmail());
//        }
//        if (employeeDetails.getDepartment() != null) {
//            employee.setDepartment(employeeDetails.getDepartment());
//        }
//        if (employeeDetails.getPosition() != null) {
//            employee.setPosition(employeeDetails.getPosition());
//        }
//        if (employeeDetails.getEmployer() != null) {
//            employee.setEmployer(employeeDetails.getEmployer());
//        }
//        if (employeeDetails.getSkills() != null && !employeeDetails.getSkills().isEmpty()) {
//            employee.setSkills(employeeDetails.getSkills());
//        }
//        if (employeeDetails.getProjects() != null && !employeeDetails.getProjects().isEmpty()) {
//            employee.setProjects(employeeDetails.getProjects());
//        }

        log.info("Saving updated employee: {}", employee);
        return employeeRepository.save(employee);
    }

    @Transactional
    @CacheEvict(value = "employees", allEntries = true)
    public void deleteEmployee(Long id) {
        log.info("Deleting employee with id: {}", id);
        Employee employee = getEmployeeById(id);
        employeeRepository.delete(employee);
        log.info("Employee deleted successfully with id: {}", id);
    }
}
