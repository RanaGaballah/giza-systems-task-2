package com.employee.employee_management_system.service;

import com.employee.employee_management_system.exception.EmployeeNotFoundException;
import com.employee.employee_management_system.model.Employee;
import com.employee.employee_management_system.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    // Constant for the error message
    private static final String EMPLOYEE_NOT_FOUND = "Employee not found with ID: ";

    // Constructor Injection for dependency injection
    @Autowired
    public EmployeeService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    // Fetch all employees
    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }

    // Fetch an employee by ID
    public Employee getEmployeeById(Long id) {
        return employeeRepository.findById(id).orElseThrow(() -> new EmployeeNotFoundException(EMPLOYEE_NOT_FOUND + id)); // Throws custom exception if not found
    }

    // Create a new employee
    public Employee addEmployee(Employee employee) {
        return employeeRepository.save(employee);
    }


    // Update an existing employee
    public Employee updateEmployee(Long id, Employee updatedEmployee) {
        return employeeRepository.findById(id).map(employee -> {
            employee.setName(updatedEmployee.getName());
            employee.setDepartment(updatedEmployee.getDepartment());
            return employeeRepository.save(employee);
        }).orElseThrow(() -> new EmployeeNotFoundException(EMPLOYEE_NOT_FOUND + id));  // Throws custom exception if not found
    }

    // Delete an employee by ID
    public void deleteEmployee(Long id) {
    	Employee employee = employeeRepository.findById(id).orElseThrow(() -> new EmployeeNotFoundException(EMPLOYEE_NOT_FOUND + id));  // Throws custom exception if not found
        employeeRepository.delete(employee);
    }


}
