package com.example.Inventory_Management.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "product")
public class Product {

    @Id
    private Integer id;
    private String name;
    private Integer categoryId;
    private Integer quantity;
    private Double price;

    // @ManyToOne
    // @JoinColumn(name = "categoryId")
    // private Category category;

}
