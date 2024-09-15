package com.Aspire.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document(collection = "stream")
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
