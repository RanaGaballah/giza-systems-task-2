package com.employee.employee_management_system.service;

import com.employee.employee_management_system.repository.EmployeeRepository;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import com.employee.employee_management_system.exception.EmployeeNotFoundException;
import com.employee.employee_management_system.model.Employee;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import java.util.Optional;
import java.util.List;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EmployeeServiceTest {

	@Mock
	private EmployeeRepository employeeRepository; // Mocked repository dependency

	@InjectMocks
	private EmployeeService employeeService; // Inject mock into the service

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this); // Initialize mocks before each test
	}

	@Test
	void testGetAllEmployees() {
		// Arrange: Create a list of mock employees
		List<Employee> mockEmployees = Arrays.asList(new Employee(1L, "Rana Gaballah", "SW"),
				new Employee(2L, "Mohamed Saaed", "Engineering"));

		// Mock the repository response
		when(employeeRepository.findAll()).thenReturn(mockEmployees);

		// Act: Call the service method
		List<Employee> employees = employeeService.getAllEmployees();

		// Assert: Verify the correct behavior
		assertEquals(2, employees.size()); // Verify number of employees
		assertEquals("Rana Gaballah", employees.get(0).getName()); // Verify first employee's name
		assertEquals("Mohamed Saaed", employees.get(1).getName()); // Verify second employee's name
		verify(employeeRepository, times(1)).findAll(); // Verify that findAll was called once
	}

	@Test
	void testGetEmployeeById_ValidId() {
		// Arrange: Create a mock employee
		Employee mockEmployee = new Employee(1L, "Rana Gaballah", "SW");

		// Mock repository findById method
		when(employeeRepository.findById(1L)).thenReturn(Optional.of(mockEmployee));

		// Act: Call the service method
		Employee employee = employeeService.getEmployeeById(1L);

		// Assert: Verify the correct behavior
		assertNotNull(employee);
		assertEquals("Rana Gaballah", employee.getName()); // Verify employee's name
		verify(employeeRepository, times(1)).findById(1L); // Verify findById is called once
	}

	@Test
	void testGetEmployeeById_InvalidId() {
		// Arrange: Mock repository to return empty when employee not found
		when(employeeRepository.findById(1L)).thenReturn(Optional.empty());

		// Act & Assert: Ensure exception is thrown for invalid ID
		EmployeeNotFoundException exception = assertThrows(EmployeeNotFoundException.class,
				() -> employeeService.getEmployeeById(1L));

		assertEquals("Employee not found with ID: 1", exception.getMessage()); // Check exception message
		verify(employeeRepository, times(1)).findById(1L); // Verify findById is called
	}

	@Test
	void testAddEmployee() {
		// Arrange: Create a mock employee and mock the save call
		Employee mockEmployee = new Employee(1L, "Mohammed Saaed", "SW");
		when(employeeRepository.save(any(Employee.class))).thenReturn(mockEmployee);

		// Act: Call the service method
		Employee savedEmployee = employeeService.addEmployee(mockEmployee);

		// Assert: Check that the returned employee matches the mock
		assertNotNull(savedEmployee);
		assertEquals("Mohammed Saaed", savedEmployee.getName()); // Verify employee's name
		verify(employeeRepository, times(1)).save(mockEmployee); // Verify save is called once
	}

	@Test
	void testUpdateEmployee_ValidId() {
		// Arrange: Create mock existing employee and updated employee
		Employee existingEmployee = new Employee(1L, "Rana Gaballah", "SW");
		Employee updatedEmployee = new Employee("Rana Mahmoud", "Full Stack");

		// Mock repository findById and save methods
		when(employeeRepository.findById(1L)).thenReturn(Optional.of(existingEmployee));
		when(employeeRepository.save(any(Employee.class))).thenReturn(existingEmployee);

		// Act: Call the update service method
		Employee result = employeeService.updateEmployee(1L, updatedEmployee);

		// Assert: Verify the employee was updated correctly
		assertNotNull(result);
		assertEquals("Rana Mahmoud", result.getName()); // Verify updated employee's name
		assertEquals("Full Stack", result.getDepartment()); // Verify updated employee's department
		verify(employeeRepository, times(1)).findById(1L); // Ensure findById is called
		verify(employeeRepository, times(1)).save(existingEmployee); // Ensure save is called with updated employee
	}

	@Test
	void testUpdateEmployee_InvalidId() {
		// Arrange: Mock repository to return empty for invalid ID
		Employee updatedEmployee = new Employee("Rana Gabllah", "SW");
		when(employeeRepository.findById(1L)).thenReturn(Optional.empty());

		// Act & Assert: Ensure exception is thrown for invalid ID
		EmployeeNotFoundException exception = assertThrows(EmployeeNotFoundException.class,
				() -> employeeService.updateEmployee(1L, updatedEmployee));

		assertEquals("Employee not found with ID: 1", exception.getMessage()); // Verify exception message
		verify(employeeRepository, times(1)).findById(1L); // Verify findById is called
		verify(employeeRepository, times(0)).save(any(Employee.class)); // Ensure save is not called
	}

	@Test
	void testDeleteEmployee_ValidId() {
		// Arrange: Create a mock employee and mock the findById and delete
		Employee mockEmployee = new Employee(1L, "Rana Gaballah", "SW");
		when(employeeRepository.findById(1L)).thenReturn(Optional.of(mockEmployee));
		doNothing().when(employeeRepository).delete(mockEmployee);

		// Act: Call the delete service method
		employeeService.deleteEmployee(1L);

		// Assert: Verify delete operation
		verify(employeeRepository, times(1)).delete(mockEmployee); // Ensure delete was called
	}

	@Test
	void testDeleteEmployee_InvalidId() {
		// Arrange: Mock repository to return empty
		when(employeeRepository.findById(1L)).thenReturn(Optional.empty());

		// Act & Assert: Ensure exception is thrown for invalid ID
		EmployeeNotFoundException exception = assertThrows(EmployeeNotFoundException.class,
				() -> employeeService.deleteEmployee(1L));

		assertEquals("Employee not found with ID: 1", exception.getMessage());
		verify(employeeRepository, times(1)).findById(1L); // Verify findById was called
	}
}
