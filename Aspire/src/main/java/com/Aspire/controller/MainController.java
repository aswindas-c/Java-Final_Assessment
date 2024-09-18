package com.Aspire.controller;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.Aspire.DTO.Response;
import com.Aspire.model.Employee;
import com.Aspire.service.EmployeeService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@RequestMapping("/api/employee")

public class MainController {
    
    @Autowired
    private EmployeeService employeeService;
    
    //Add an employee
    @PostMapping("/add")
    public Response addEmployee(@Validated @RequestBody Employee employee) {
        return employeeService.addEmployee(employee);
    }

    //Return employees starting with given character
    @GetMapping()
    public Map<String, Object> getEmployee(@RequestParam(required = false) String startsWith) {
        List<Employee> employees = employeeService.getEmployee(startsWith);
        
        Map<String, Object> response = new HashMap<>();
        response.put("Employees", employees);
        
        return response;
    }

    //Get all streams
    @GetMapping("/streams")
    public Map<String, Object> getStreams() {
        List<String> streams = employeeService.getStreams();
        Map<String, Object> response = new HashMap<>();
        response.put("streams", streams);
        return response;
    }

    //Delete an employee
    @DeleteMapping("/delete")
    public Response deleteEmployee(@RequestParam Integer employeeId) {
        return employeeService.deleteEmployee(employeeId);
    }

    //Change a manager of a employee
    @PutMapping("/changeManager")
    public Response changeManager(
        @RequestParam Integer employeeId,
        @RequestParam Integer managerId) {
        return employeeService.changeManager(employeeId, managerId);
    }

    //Change designation of a employee
    @PutMapping("/changeDesignation")
    public Response changeDesignation(
        @RequestParam Integer employeeId,
        @RequestParam String stream) {
        return employeeService.changeDesignation(employeeId, stream);
    }
}
