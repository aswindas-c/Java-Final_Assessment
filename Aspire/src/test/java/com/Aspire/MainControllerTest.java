package com.Aspire;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import static org.hamcrest.Matchers.hasSize;

import com.Aspire.DTO.Response;
import com.Aspire.controller.MainController;
import com.Aspire.model.Employee;
import com.Aspire.model.Stream;
import com.Aspire.service.EmployeeService;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.hamcrest.Matchers.equalTo;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest (MainController.class)
@ExtendWith (MockitoExtension.class)
@AutoConfigureMockMvc
class MainControllerTest {

    @Autowired
    MockMvc mvc;

    @MockBean
    EmployeeService employeeService;

    private ObjectMapper objectMapper = new ObjectMapper();

    //Add a Employee
    @Test
    void testAdd_Product() {
        Employee employee = new Employee();
        employee.setName("Aswin");
        employee.setId(1);
        when(employeeService.addEmployee(employee)).thenReturn(new Response("Employee added successfully"));
        try {
            mvc.perform(MockMvcRequestBuilders.post("/api/employee/add")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(employee)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message", equalTo("Employee added successfully")));
            
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    //Get employee starting with
    @Test
    void testGet_Employee_Startingwith() {
        Employee employee1 = new Employee();
        employee1.setName("Aswin");
        Employee employee2 = new Employee();
        employee2.setName("Amal");
        List<Employee> employeeList = Arrays.asList(employee1,employee2);
        when(employeeService.getEmployee("A")).thenReturn(employeeList);

        try {
            mvc.perform(MockMvcRequestBuilders.get("/api/employee")
                    .param("startsWith", "A")
                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.Employees", hasSize(2)))
                    .andExpect(jsonPath("$.Employees[0].name", equalTo("Aswin")))
                    .andExpect(jsonPath("$.Employees[1].name", equalTo("Amal")));
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    //Get all streams
    @Test
    void testGet_Streams() {
        Stream stream = new Stream();
        List<String> streamList = Arrays.asList(stream.getName());
        when(employeeService.getStreams()).thenReturn(streamList);

        try {
            mvc.perform(MockMvcRequestBuilders.get("/api/employee/streams")
                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.streams[0]", equalTo(stream.getName())));
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    //Change Employee Manager
    @Test
    void testPut_Change_Manager() {
        when(employeeService.changeManager(2,3)).thenReturn(new Response("Arun's manager has been successfully changed from Akhil to Aswin."));
        try {
            mvc.perform(MockMvcRequestBuilders.put("/api/employee/changeManager")
                    .param("employeeId", "2")
                    .param("managerId", "3")
                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message", equalTo("Arun's manager has been successfully changed from Akhil to Aswin.")));
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    //Change Designation
    @Test
    void testPut_Change_Designation() {
        when(employeeService.changeDesignation(2,"SmartOps-Sales")).thenReturn(new Response("Arun has been promoted to Manager of SmartOps-Sales"));
        try {
            mvc.perform(MockMvcRequestBuilders.put("/api/employee/changeDesignation")
                    .param("employeeId", "2")
                    .param("stream", "SmartOps-Sales")
                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message", equalTo("Arun has been promoted to Manager of SmartOps-Sales")));
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    //Change Account
    @Test
    void testPut_Change_Account() {
        when(employeeService.changeAccount(2,"SmartOps","SmartOps-Sales")).thenReturn(new Response("Arun account has been changed"));
        try {
            mvc.perform(MockMvcRequestBuilders.put("/api/employee/changeAccount")
                    .param("employeeId", "2")
                    .param("account", "SmartOps")
                    .param("stream", "SmartOps-Sales")
                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message", equalTo("Arun account has been changed")));
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }
}