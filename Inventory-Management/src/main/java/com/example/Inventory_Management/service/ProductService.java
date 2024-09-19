package com.example.Inventory_Management.service;

import java.util.List;
import java.util.NoSuchElementException;

import javax.management.openmbean.KeyAlreadyExistsException;

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

        // Find the maximum id and increment for next id
        Integer maxId = productRepo.findMaxId();
        if(maxId != null) {
            product.setId(maxId + 1);
        } else {
            product.setId(1);
        }
        // Check if the product name already exists (it should be unique)
        if (productRepo.findByName(product.getName()) != null) {
            throw new KeyAlreadyExistsException("Product name already exists.");
        }
        
        // Check if product with the same ID already exists
        if (productRepo.existsById(product.getId())) {
            throw new KeyAlreadyExistsException("Product ID already exists.");
        }
        // Category category = categoryRepo.findById(product.getCategory().getId()).orElse(null);
        // if (category == null) {
        //     throw new NoSuchElementException("Error: Category does not exist.");
        // }

        // Check if the category id exist
        if (!categoryRepo.existsById(product.getCategoryId())) {
            throw new NoSuchElementException("Category does not exist.");
        }

        productRepo.save(product);
        return new Response("Product added successfully with ID: " + product.getId());
    }
    //Get products based on product id or category id or both
    public List<Product> getProduct(Integer productId, Integer categoryId) {
        List<Product> products;
        if (productId != null && categoryId != null) {
            products = productRepo.findByCategoryIdandId(productId,categoryId);
            if(products.isEmpty())
            {
                throw new NoSuchElementException("No Employee exists under that category id and product id");
            }
            return products;
        } 
        else if(categoryId != null) 
        {
            products = productRepo.findByCategoryId(categoryId);
            if(products.isEmpty())
            {
                throw new NoSuchElementException("No Product exists under that category id.");
            }
            return products;
        }
        else if(productId != null)
        {
            products = productRepo.findUsingId(productId);
            System.out.println(products);
            if(products.isEmpty())
            {
                throw new NoSuchElementException("No Product exists with that product id.");
            }
            return products;
        }
        else
        {
            if(productRepo.findAll().isEmpty())
            {
                throw new NoSuchElementException("No Products found.");
            }
            return productRepo.findAll();
        }
    }

    //add category
    public Response addCategory(Category category) {

        // Check if the category name already exists (it should be unique)
        if (categoryRepo.findByName(category.getName()) != null) {
            throw new KeyAlreadyExistsException("Category name already exists.");
        }

        // Find the maximum ID of categories and assign next id to the new category
        Integer maxId = categoryRepo.findMaxId();
        if(maxId != null) {
            category.setId(maxId + 1);
        } else {
            category.setId(1);
        }
        
        // Check if category with the same ID already exists
        if (categoryRepo.existsById(category.getId())) {
            throw new KeyAlreadyExistsException("Category ID already exists.");
        }
        categoryRepo.save(category);
        return new Response("Category added successfully with ID: " + category.getId());
    }
}
