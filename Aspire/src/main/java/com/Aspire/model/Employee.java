package com.Aspire.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "employee")
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
    
    @ManyToOne
    @JoinColumn(name = "stream", referencedColumnName = "name", insertable=false, updatable=false)
    private Stream streamobj;

    private String accountName; 

    @ManyToOne
    @JoinColumn(name = "accountName", referencedColumnName = "name", insertable=false, updatable=false)
    private Account account;

}