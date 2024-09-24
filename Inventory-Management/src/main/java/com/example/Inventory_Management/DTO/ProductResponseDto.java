package com.example.Inventory_Management.DTO;

import com.example.Inventory_Management.model.Product;

import lombok.Data;

@Data
public class ProductResponseDto {
    private Integer id; 
    private String name; 
    private Integer quantity; 
    private Double price; 
    private Integer categoryId; 

    public ProductResponseDto(Product product) {
        this.id = product.getId();
        this.name = product.getName();
        this.quantity = product.getQuantity();
        this.price = product.getPrice();
        this.categoryId = product.getCategory().getId();
    }

}