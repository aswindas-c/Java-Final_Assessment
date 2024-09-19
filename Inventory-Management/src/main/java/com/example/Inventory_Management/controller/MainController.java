package com.example.Inventory_Management.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.Inventory_Management.DTO.Response;
import com.example.Inventory_Management.model.Category;
import com.example.Inventory_Management.model.Product;
import com.example.Inventory_Management.repository.CategoryRepo;
import com.example.Inventory_Management.service.ProductService;

@RestController
@RequestMapping("/api")
public class MainController {

    @Autowired
    private ProductService productService;

    @Autowired
    CategoryRepo categoryRepo;

    @PostMapping("/addProduct")
    public Response addEmployee(@RequestBody Product product) {
        return productService.addProduct(product);
    }

    @PostMapping("/addCategory")
    public Response addCategory(@RequestBody Category category) {
        return productService.addCategory(category);
    }

}
