package com.Aspire.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document(collection = "manager")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Manager {
    
    @Id
    private Integer id;

    private String name;
    
    private String streamName;

}
