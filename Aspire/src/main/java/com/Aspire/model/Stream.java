package com.Aspire.model;


import jakarta.persistence.Entity;
import jakarta.persistence.Id;
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

    private String name;

    private String accountId;

    private Integer managerId;
}
