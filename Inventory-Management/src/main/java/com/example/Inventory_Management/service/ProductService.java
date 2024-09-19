package com.example.Inventory_Management.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.Inventory_Management.DTO.Response;
import com.example.Inventory_Management.model.Category;
import com.example.Inventory_Management.model.Product;
import com.example.Inventory_Management.repository.CategoryRepo;
import com.example.Inventory_Management.repository.ProductRepo;

@Service
public class ProductService {

    @Autowired
    ProductRepo productRepo;

    @Autowired
    CategoryRepo categoryRepo;

    public Response addProduct(Product product) {

        // Check if the product name already exists (it should be unique)
        if (productRepo.findByName(product.getName()) != null) {
            return new Response("Error: Product name already exists.");
        }

        // Check if the category id exist
        if (!categoryRepo.existsById(product.getCategoryId())) {
            return new Response("Error: Category does not exist.");
        }

        // Find the maximum ID of employees and assign next id to the new employee
        Integer maxId = productRepo.findMaxId();
        if(maxId != null) {
            product.setId(maxId + 1);
        } else {
            product.setId(1);
        }
        
        productRepo.save(product);
        return new Response("Product added successfully with ID: " + product.getId());
    }

//CATEGORY_SERVICE
    public Response addCategory(Category category) {

        // Check if the category name already exists (it should be unique)
        if (categoryRepo.findByName(category.getName()) != null) {
            return new Response("Error: Category name already exists.");
        }

        // Find the maximum ID of categories and assign next id to the new category
        Integer maxId = categoryRepo.findMaxId();
        if(maxId != null) {
            category.setId(maxId + 1);
        } else {
            category.setId(1);
        }
        
        categoryRepo.save(category);
        return new Response("Category added successfully with ID: " + category.getId());
    }

   
}
