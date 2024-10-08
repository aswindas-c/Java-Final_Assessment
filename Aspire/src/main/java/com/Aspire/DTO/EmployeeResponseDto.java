package com.Aspire.DTO;

import com.Aspire.model.Employee;

import lombok.AllArgsConstructor;
import lombok.Data;
@AllArgsConstructor
@Data
public class EmployeeResponseDto {
    private Integer id; 
    private String name; 
    private String designation;
    private Integer managerId; 
    private String stream; 
    private String accounName; 

    public EmployeeResponseDto(Employee employee) {
        this.id = employee.getId();
        this.name = employee.getName();
        this.designation = employee.getDesignation();
        this.managerId = employee.getManagerId();
        this.stream = employee.getStream();
        this.accounName = employee.getAccountName();
    }

}
