package com.Aspire.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
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

    // @GetMapping("/getEmployee")
    // public List<ManagerResponse> getEmployee(
    //         @RequestParam(required = false) Integer managerId,
    //         @RequestParam(required = false) Integer yearsOfExperience) {
    //     return employeeService.getEmployee(managerId, yearsOfExperience);
    // }

    @DeleteMapping("/deleteEmployee")
    public Response deleteEmployee(@RequestParam Integer employeeId) {
        return employeeService.deleteEmployee(employeeId);
    }

    // @PutMapping("/changeEmployeeManager")
    // public Response changeEmployeeManager(
    //         @RequestBody ChangeManagerRequest request) {
    //     return employeeService.changeEmployeeManager(request.getEmployeeId(), request.getManagerId());
    // }
}
