package com.example.Inventory_Management.DTO;

import com.example.Inventory_Management.model.Product;

import lombok.Data;

@Data
public class ProductResponseDto {
    private Integer id; // Include product ID
    private String name; // Include product name
    private Integer quantity; // Include product quantity
    private Double price; // Include product price
    private Integer categoryId; // Include only category ID

    public ProductResponseDto(Product product) {
        this.id = product.getId();
        this.name = product.getName();
        this.quantity = product.getQuantity();
        this.price = product.getPrice();
        this.categoryId = product.getCategory().getId();
    }

}