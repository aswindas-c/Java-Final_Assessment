package com.example.Inventory_Management.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductDto {
    private String name;
    private Integer categoryId;
    private Double price;
    private Integer quantity;
}
