package com.Aspire.service;

import java.util.List;
import java.util.NoSuchElementException;

import javax.management.openmbean.KeyAlreadyExistsException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.Aspire.DTO.Response;
import com.Aspire.Respository.EmployeeRepository;
import com.Aspire.model.Employee;

@Service
public class EmployeeService {

    @Autowired
    private EmployeeRepository employeeRepo;

    public Response addEmployee(Employee employee) {


        // String designation = employee.getDesignation();
        // String stream = employee.getStream();
        // String accountName = employee.getAccountName();

        Integer maxId = employeeRepo.findMaxId();
        if(maxId != null) {
            employee.setId(maxId + 1);
        } else {
            employee.setId(1);
        }
        
        // Check if employee with the same ID already exists
        if (employeeRepo.existsById(employee.getId())) {
            throw new KeyAlreadyExistsException("Employee ID already exists.");
        }

        // Handle special case for Account Manager
        if ("Account Manager".equalsIgnoreCase(employee.getDesignation())) {
            if (employee.getManagerId() != 0) {
                throw new IllegalArgumentException("Account Manager must have Manager ID set to 0. Employee cannot be added.");
            }

            // Check if a manager already exists in the stream            
            List<Employee> existingManagers = employeeRepo.findByStream(employee.getStream());

            if (!existingManagers.isEmpty()) {
                throw new KeyAlreadyExistsException("A manager already exists in the stream: " + employee.getStream());
            }

            employeeRepo.insert(employee);
            return new Response("Employee added as Manager successfully with ID: " + employee.getId());
        } else {
            // Handle non-Account Manager 
            if (employee.getManagerId() == 0) {
                throw new IllegalArgumentException("Manager ID 0 should have designation as Account Manager. Employee cannot be added.");
            }
        }

        // Handle normal employee
        Employee manager = employeeRepo.findById(employee.getManagerId());
        if (manager == null) {
            throw new NoSuchElementException("Manager with ID " + employee.getManagerId() + " not found. Employee cannot be added.");
        }

        if (manager.getManagerId() != 0) {
            throw new NoSuchElementException("Employee with ID " + employee.getManagerId() + " is not a manager. Employee cannot be added.");
        }

        // Check if employee and manager are in the same stream
        if (!employee.getStream().equalsIgnoreCase(manager.getStream())) {
            throw new IllegalArgumentException("Employee and manager must belong to the same department. Employee cannot be added.");
        }

        // Save the employee
        employeeRepo.insert(employee);

        return new Response("Employee added successfully under Manager with ID: " + manager.getId());
    }
}
