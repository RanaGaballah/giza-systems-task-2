package com.employee.employee_management_system.controller;

import com.employee.employee_management_system.exception.EmployeeNotFoundException;
import com.employee.employee_management_system.model.Employee;
import com.employee.employee_management_system.service.EmployeeService;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

	private final EmployeeService employeeService;

	// Constructor Injection for dependency injection
	public EmployeeController(EmployeeService employeeService) {
		this.employeeService = employeeService;
	}

	// GET: Fetch all employees
	@GetMapping
	public ResponseEntity<List<Employee>> getAllEmployees() {
		return new ResponseEntity<>(employeeService.getAllEmployees(), HttpStatus.OK);
	}

	// GET: Fetch employee by ID
	@GetMapping("/{id}")
	public ResponseEntity<Employee> getEmployeeById(@PathVariable Long id) {
		return new ResponseEntity<>(employeeService.getEmployeeById(id), HttpStatus.OK);
	}

	// POST: Create a new employee
	@PostMapping
	public ResponseEntity<Employee> addEmployee(@Valid @RequestBody Employee employee) {
		return new ResponseEntity<>(employeeService.addEmployee(employee), HttpStatus.CREATED);
	}

	// PUT: Update an existing employee
	@PutMapping("/{id}")
	public ResponseEntity<Employee> updateEmployee(@PathVariable Long id,
			@Valid @RequestBody Employee updatedEmployee) {
		return new ResponseEntity<>(employeeService.updateEmployee(id, updatedEmployee), HttpStatus.OK);
	}

	// DELETE: Remove an employee by ID
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteEmployee(@PathVariable Long id) {
		employeeService.deleteEmployee(id);
		return ResponseEntity.noContent().build();
	}

}
