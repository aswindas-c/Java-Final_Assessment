package com.example.Inventory_Management.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.Inventory_Management.DTO.Response;
import com.example.Inventory_Management.model.Product;
import com.example.Inventory_Management.repository.ProductRepo;

@Service
public class ProductService {

    @Autowired
    ProductRepo productRepo;

    public Response addProduct(Product product) {

        // Find the maximum ID of employee
        Integer maxId = productRepo.findMaxId();
        if(maxId != null) {
            product.setId(maxId + 1);
        } else {
            product.setId(1);
        }
        //Check if the product name already exists(it should be unique)
        //Check if the category id exist
        productRepo.save(product);
        return new Response("Product added successfully with ID: " + product.getId());
    }
   
}
