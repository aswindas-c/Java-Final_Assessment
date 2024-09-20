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
@Table(name = "stream")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Stream {
    @Id
    private String id;
    
    @ManyToOne
    @JoinColumn(name = "account_id")
    private Account account;

    private Integer managerId;

    private String name;

}
