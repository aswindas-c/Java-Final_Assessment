package com.Aspire.controller;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StopWatch;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.apache.log4j.Logger;
import com.Aspire.DTO.EmployeeResponseDto;
import com.Aspire.DTO.Response;
import com.Aspire.model.Employee;
import com.Aspire.service.EmployeeService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@RequestMapping("/api/employee")

public class MainController {
    
    private Logger logger = Logger.getLogger(MainController.class);

    @Autowired
    private EmployeeService employeeService;
    
    //Add an employee
    @PostMapping("/add")
    public Response addEmployee(@Validated @RequestBody Employee employee) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        Response response =  employeeService.addEmployee(employee);

        stopWatch.stop();
        logger.info("Add-Employee Query executed in " + stopWatch.getTotalTimeMillis() + "ms");

        return response;
    }

    //Return employees starting with given character
    @GetMapping()
    public Map<String, Object> getEmployee(@RequestParam(required = false) String startsWith) {

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        List<EmployeeResponseDto> employees = employeeService.getEmployee(startsWith);
        
        Map<String, Object> response = new HashMap<>();
        response.put("Employees", employees);
        
        stopWatch.stop();
        logger.info("Get-Employee Query executed in " + stopWatch.getTotalTimeMillis() + "ms");

        return response;
    }

    //Get all streams
    @GetMapping("/streams")
    public Map<String, Object> getStreams() {

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        List<String> streams = employeeService.getStreams();
        Map<String, Object> response = new HashMap<>();
        response.put("streams", streams);

        stopWatch.stop();
        logger.info("Get-Streams Query executed in " + stopWatch.getTotalTimeMillis() + "ms");

        return response;
    }

    //Delete an employee
    @DeleteMapping("/delete")
    public Response deleteEmployee(@RequestParam Integer employeeId) {

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        Response response = employeeService.deleteEmployee(employeeId);

        stopWatch.stop();
        logger.info("Delete - Employee Query executed in " + stopWatch.getTotalTimeMillis() + "ms");

        return response;
    }

    //Change a manager of a employee
    @PutMapping("/changeManager")
    public Response changeManager(
        @RequestParam Integer employeeId,
        @RequestParam Integer managerId) {
            
            StopWatch stopWatch = new StopWatch();
            stopWatch.start();

            Response response = employeeService.changeManager(employeeId, managerId);

            stopWatch.stop();
            logger.info("Change-Manager Query executed in " + stopWatch.getTotalTimeMillis() + "ms");

            return response;
    }

    //Change designation of a employee
    @PutMapping("/changeDesignation")
    public Response changeDesignation(
        @RequestParam Integer employeeId,
        @RequestParam String stream) {

            StopWatch stopWatch = new StopWatch();
            stopWatch.start();

            Response response = employeeService.changeDesignation(employeeId, stream);

            stopWatch.stop();
            logger.info("Change-Designation Query executed in " + stopWatch.getTotalTimeMillis() + "ms");

            return response;
    }

    //Change Account of a employee
    @PutMapping("/changeAccount")
    public Response changeAccount(
        @RequestParam Integer employeeId,
        @RequestParam String account,
        @RequestParam String stream) {

            StopWatch stopWatch = new StopWatch();
            stopWatch.start();

            Response response = employeeService.changeAccount(employeeId, account, stream);

            stopWatch.stop();
            logger.info("Change-Account Query executed in " + stopWatch.getTotalTimeMillis() + "ms");

            return response;
    }
}
