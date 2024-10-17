package com.employee.employee_management_system.controller;

import com.employee.employee_management_system.exception.EmployeeNotFoundException;
import com.employee.employee_management_system.model.Employee;
import com.employee.employee_management_system.service.EmployeeService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EmployeeController.class)
public class EmployeeControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private EmployeeService employeeService;

	@Autowired
	private ObjectMapper objectMapper; // To serialize objects to JSON

	private Employee employee1;
	private Employee employee2;

	@BeforeEach
	void setUp() {
		employee1 = new Employee(1L, "Rana", "Developer");
		employee2 = new Employee(2L, "Mohamed", "Designer");
	}

	@Test
	@WithMockUser(username = "user", roles = { "USER" })
	void testGetAllEmployees_success() throws Exception {
		List<Employee> employees = Arrays.asList(employee1, employee2);
		when(employeeService.getAllEmployees()).thenReturn(employees);

		mockMvc.perform(get("/api/employees")).andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(2)))
				.andExpect(jsonPath("$[0].name", is(employee1.getName())))
				.andExpect(jsonPath("$[1].name", is(employee2.getName())));
	}

	@Test
	@WithMockUser(username = "user", roles = { "USER" })
	void testGetEmployeeById_success() throws Exception {
		when(employeeService.getEmployeeById(1L)).thenReturn(employee1);

		mockMvc.perform(get("/api/employees/{id}", 1L)).andExpect(status().isOk())
				.andExpect(jsonPath("$.name", is(employee1.getName())))
				.andExpect(jsonPath("$.department", is(employee1.getDepartment())));
	}

	@Test
	@WithMockUser(username = "user", roles = { "USER" })
	void testGetEmployeeById_notFound() throws Exception {
		when(employeeService.getEmployeeById(1L))
				.thenThrow(new EmployeeNotFoundException("Employee not found with id: 1"));

		mockMvc.perform(get("/api/employees/{id}", 1L)).andExpect(status().isNotFound());
	}

	@Test
	@WithMockUser(username = "user", roles = { "USER" })
	void testCreateEmployee_success() throws Exception {
		when(employeeService.addEmployee(any(Employee.class))).thenReturn(employee1);

		mockMvc.perform(post("/api/employees").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(employee1)).with(csrf())).andExpect(status().isCreated())
				.andExpect(jsonPath("$.name", is(employee1.getName())))
				.andExpect(jsonPath("$.department", is(employee1.getDepartment())));
	}

	@Test
	@WithMockUser(username = "user", roles = { "USER" })
	void testCreateEmployee_validationFailure() throws Exception {
		Employee invalidEmployee = new Employee(null, "", "Developer");

		mockMvc.perform(post("/api/employees").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(invalidEmployee)).with(csrf()))
				.andExpect(status().isBadRequest());
	}

	@Test
	@WithMockUser(username = "user", roles = { "USER" })
	void testUpdateEmployee_success() throws Exception {
		when(employeeService.updateEmployee(Mockito.eq(1L), any(Employee.class))).thenReturn(employee1);

		mockMvc.perform(put("/api/employees/{id}", 1L).contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(employee1)).with(csrf())).andExpect(status().isOk())
				.andExpect(jsonPath("$.name", is(employee1.getName())));
	}

	@Test
	@WithMockUser(username = "user", roles = { "USER" })
	void testUpdateEmployee_notFound() throws Exception {
		when(employeeService.updateEmployee(Mockito.eq(1L), any(Employee.class)))
				.thenThrow(new EmployeeNotFoundException("Employee not found with id: 1"));

		mockMvc.perform(put("/api/employees/{id}", 1L).contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(employee1)).with(csrf())).andExpect(status().isNotFound());
	}

	@Test
	@WithMockUser(username = "admin", roles = { "ADMIN" })
	void testDeleteEmployee_success() throws Exception {
		doNothing().when(employeeService).deleteEmployee(1L);

		mockMvc.perform(delete("/api/employees/{id}", 1L).with(csrf())).andExpect(status().isNoContent());
	}

	@Test
	@WithMockUser(username = "admin", roles = { "ADMIN" })
	void testDeleteEmployee_notFound() throws Exception {
		doThrow(new EmployeeNotFoundException("Employee not found with ID: " + 999L)).when(employeeService)
				.deleteEmployee(999L);

		mockMvc.perform(delete("/api/employees/{id}", 999L).with(csrf())).andExpect(status().isNotFound());
	}

	@Test
	void testUnauthorizedAccess() throws Exception {
		mockMvc.perform(get("/api/employees")).andExpect(status().isUnauthorized());
	}

	@Test
	@WithMockUser(username = "user", roles = { "USER" })
	void testUnauthorizedAccess_deleteEmployeeById() throws Exception {
		mockMvc.perform(delete("/api/employees/{id}", employee1.getId())).andExpect(status().isForbidden());
	}
}
