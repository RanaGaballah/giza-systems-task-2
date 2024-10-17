package com.employee.employee_management_system.controller;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.employee.employee_management_system.model.Employee;
import com.employee.employee_management_system.repository.EmployeeRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles(profiles = "test")
@Tag("integration")
@Transactional
@Rollback
public class EmployeeAPIIT {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private EmployeeRepository employeeRepository;

	@Autowired
	private ObjectMapper objectMapper;

	@BeforeEach
	void setup() {
		employeeRepository.deleteAll();
	}
	
	//Success Tests//

	@Test
	@WithMockUser(username = "user", roles = { "USER" })
	void testGetAllEmployees_emptyList() throws Exception {
		mockMvc.perform(get("/api/employees")).andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(0)));
	}

	@Test
	@WithMockUser(username = "user", roles = { "USER" })
	void testGetAllEmployees_success() throws Exception {
		employeeRepository.save(new Employee("Mohamed", "CS"));
		employeeRepository.save(new Employee("Rana", "CS"));
		mockMvc.perform(get("/api/employees")).andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(2)))
				.andExpect(jsonPath("$[0].name", is("Mohamed"))).andExpect(jsonPath("$[0].department", is("CS")))
				.andExpect(jsonPath("$[1].name", is("Rana"))).andExpect(jsonPath("$[1].department", is("CS")));
	}

	@Test
	@WithMockUser(username = "user", roles = { "USER" })
	void testGetEmployeeById_success() throws Exception {
		Employee employee = employeeRepository.save(new Employee("Mohamed", "CS"));
		mockMvc.perform(get("/api/employees/{id}", employee.getId())).andExpect(status().isOk())
				.andExpect(jsonPath("$.name", is("Mohamed"))).andExpect(jsonPath("$.department", is("CS")));
	}

	@Test
	@WithMockUser(username = "user", roles = { "USER" })
	void testCreateEmployee_success() throws Exception {
		Employee employee = new Employee("Ahmed", "CS");
		mockMvc.perform(post("/api/employees").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(employee))).andExpect(status().isCreated())
				.andExpect(jsonPath("$.name", is("Ahmed"))).andExpect(jsonPath("$.department", is("CS")));
	}

	@Test
	@WithMockUser(username = "user", roles = { "USER" })
	void testUpdateEmployee_success() throws Exception {
		Employee employee1 = employeeRepository.save(new Employee("Mohamed", "CS"));
		Employee employee2 = new Employee("Ahmed", "Hardware");
		mockMvc.perform(put("/api/employees/{id}", employee1.getId()).contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(employee2))).andExpect(status().isOk())
				.andExpect(jsonPath("$.name", is(employee2.getName())));
	}

	@Test
	@WithMockUser(username = "admin", roles = { "ADMIN" })
	void testDeleteEmployee_success() throws Exception {
		Employee employee = employeeRepository.save(new Employee("Mohamed", "CS"));
		mockMvc.perform(delete("/api/employees/{id}", employee.getId())).andExpect(status().isNoContent());
	}

	// Not Found By ID Tests//

	@Test
	@WithMockUser(username = "user", roles = { "USER" })
	void testGetEmployeeByIdNotFound() throws Exception {
		mockMvc.perform(get("/api/employees/{id}", 999L).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound());
	}

	@Test
	@WithMockUser(username = "user", roles = { "USER" })
	void testUpdateEmployee_notFound() throws Exception {
		Employee employee = new Employee("Ahmed", "Hardware");
		mockMvc.perform(put("/api/employees/{id}", 999L).contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(employee))).andExpect(status().isNotFound());
	}

	@Test
	@WithMockUser(username = "admin", roles = { "ADMIN" })
	void testDeleteEmployee_notFound() throws Exception {
		mockMvc.perform(delete("/api/employees/{id}", 999L)).andExpect(status().isNotFound());
	}

	// Authorization Tests//

	@Test
	void testUnauthorizedAccess_getAllEmployees() throws Exception {
		mockMvc.perform(get("/api/employees")).andExpect(status().isUnauthorized());
	}

	@Test
	@WithMockUser(username = "user", roles = { "USER" })
	void testUnauthorizedAccess_deleteEmployeeById() throws Exception {
		Employee employee = employeeRepository.save(new Employee("Mohamed", "CS"));
		mockMvc.perform(delete("/api/employees/{id}", employee.getId())).andExpect(status().isForbidden());
	}

	// Create Validations Tests//

	@Test
	@WithMockUser(username = "user", roles = { "USER" })
	void testCreateEmployee_employeeValidationFailure() throws Exception {
		Employee invalidEmployee = new Employee("", "");
		mockMvc.perform(post("/api/employees").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(invalidEmployee))).andExpect(status().isBadRequest());
	}

	@Test
	@WithMockUser(username = "user", roles = { "USER" })
	void testCreateEmployee_employeeNameValidationFailure() throws Exception {
		Employee invalidEmployee = new Employee("", "Developer");
		mockMvc.perform(post("/api/employees").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(invalidEmployee))).andExpect(status().isBadRequest());
	}

	@Test
	@WithMockUser(username = "user", roles = { "USER" })
	void testCreateEmployee_employeeDepartmentValidationFailure() throws Exception {
		Employee invalidEmployee = new Employee("Mohamed", "");
		mockMvc.perform(post("/api/employees").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(invalidEmployee))).andExpect(status().isBadRequest());
	}

	// Update Validations Tests//

	@Test
	@WithMockUser(username = "user", roles = { "USER" })
	void testUpdateEmployee_employeeValidationFailure() throws Exception {
		Employee invalidEmployee = new Employee("", "");
		mockMvc.perform(put("/api/employees/{id}", 1L).contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(invalidEmployee))).andExpect(status().isBadRequest());
	}

	@Test
	@WithMockUser(username = "user", roles = { "USER" })
	void testUpdateEmployee_employeeNameValidationFailure() throws Exception {
		Employee invalidEmployee = new Employee("", "Developer");
		mockMvc.perform(put("/api/employees/{id}", 1L).contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(invalidEmployee))).andExpect(status().isBadRequest());
	}

	@Test
	@WithMockUser(username = "user", roles = { "USER" })
	void testUpdateEmployee_employeeDepartmentValidationFailure() throws Exception {
		Employee invalidEmployee = new Employee("Mohamed", "");
		mockMvc.perform(put("/api/employees/{id}", 1L).contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(invalidEmployee))).andExpect(status().isBadRequest());
	}

}
