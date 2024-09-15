package com.Aspire.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document(collection = "employee")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Employee {
    
    @Id
    private Integer id;

    private String name;

    private String designation;

    private Integer managerId;
    
    private String stream;

    private String accountName; 

}