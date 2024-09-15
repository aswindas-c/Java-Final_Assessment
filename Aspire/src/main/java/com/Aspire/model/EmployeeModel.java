package com.Aspire.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document(collection = "employeeManager")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeModel {
    
    @Id
    private Integer id;

    private String name;

    private Integer managerId;
    
    private String stream;

    private String accountName; 

}