package com.employee.employee_management_system.model;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "employees")
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name")
    @NotBlank(message = "Name is mandatory and cannot be empty")
    @Size(min = 2, message = "Name must have at least 2 characters")
    private String name;

    @Column(name = "department")
    @NotBlank(message = "Department is mandatory and cannot be empty")
    private String department;

    public Employee() {}


    public Employee(String name, String department) {
        this.name = name;
        this.department = department;
    }

    public Employee(Long id, String name, String department) {
        this.id = id;
        this.name = name;
        this.department = department;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }


}
