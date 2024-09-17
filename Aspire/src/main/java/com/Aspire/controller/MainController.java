package com.Aspire.controller;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
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
    
    @PostMapping("/add")
    public Response addEmployee(@Validated @RequestBody Employee employee) {
        return employeeService.addEmployee(employee);
    }

    @GetMapping()
    public Map<String, Object> getEmployee(@RequestParam(required = false) String startsWith) {
        List<Employee> employees = employeeService.getEmployee(startsWith);
        
        Map<String, Object> response = new HashMap<>();
        response.put("Employees", employees);
        
        return response;
    }
    @DeleteMapping("/delete")
    public Response deleteEmployee(@RequestParam Integer employeeId) {
        return employeeService.deleteEmployee(employeeId);
    }

    // @PutMapping("/changeEmployeeManager")
    // public Response changeEmployeeManager(
    //         @RequestBody ChangeManagerRequest request) {
    //     return employeeService.changeEmployeeManager(request.getEmployeeId(), request.getManagerId());
    // }
}
