package com.employee.employee_management_system.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

	private EmployeeErrorResponse buildErrorResponse(Exception ex, HttpStatus status) {
		return new EmployeeErrorResponse(status.value(), ex.getMessage(), System.currentTimeMillis());
	}

	// Handle EmployeeNotFound Exception
	@ExceptionHandler(EmployeeNotFoundException.class)
	public ResponseEntity<EmployeeErrorResponse> handleEmployeeNotFoundException(EmployeeNotFoundException ex) {
		return new ResponseEntity<>(buildErrorResponse(ex, HttpStatus.NOT_FOUND), HttpStatus.NOT_FOUND);
	}

	// Handle Generic Exceptions
	@ExceptionHandler(Exception.class)
	public ResponseEntity<EmployeeErrorResponse> handleException(Exception ex) {
		return new ResponseEntity<>(buildErrorResponse(ex, HttpStatus.INTERNAL_SERVER_ERROR),
				HttpStatus.INTERNAL_SERVER_ERROR);
	}

	// Handle validation errors
	@ExceptionHandler(MethodArgumentNotValidException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
		Map<String, String> errors = new HashMap<>();
		ex.getBindingResult().getAllErrors().forEach(error -> {
			String fieldName = ((FieldError) error).getField();
			String errorMessage = error.getDefaultMessage();
			errors.put(fieldName, errorMessage);
		});
		return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
	}
}
