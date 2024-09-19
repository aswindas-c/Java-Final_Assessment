package com.example.Inventory_Management.service;

import java.util.List;
import java.util.NoSuchElementException;

import javax.management.openmbean.KeyAlreadyExistsException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.Inventory_Management.DTO.ProductDto;
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

    public Response addProduct(ProductDto productDto) {

        //Stock levels cannot be negative.
        if(productDto.getQuantity()<0){
            throw new IllegalArgumentException("Stock levels cannot be negative.");
        }

        Product product = new Product();
        product.setName(productDto.getName());
        product.setCategory(categoryRepo.findById(productDto.getCategoryId())
        .orElseThrow(() -> new RuntimeException("Category does not exist")));
        product.setPrice(productDto.getPrice());
        product.setQuantity(productDto.getQuantity());
        // Check if the product name already exists (it should be unique)
        if (productRepo.findByName(product.getName()) != null) {
            throw new KeyAlreadyExistsException("Product name already exists.");
        }
        productRepo.save(product);
        return new Response("Product added successfully with ID: " + product.getId());
    }

    //updateProduct
    @Transactional
    public Response updateProduct(Integer productId, String name, Integer categoryId, Double price) {

        Product product = productRepo.findUsingId(productId);
        if(product == null){
            throw new NoSuchElementException("Product with given id does not exist");
        }
        if(name != null && categoryId != null && price != null){
            if (productRepo.findByName(product.getName()) != null) {
                throw new KeyAlreadyExistsException("Product name already exists.");
            }
            if (!categoryRepo.existsById(categoryId)) {
                throw new KeyAlreadyExistsException("Category name already exists.");
            }
            product.setName(name);
            product.setCategory(categoryRepo.findUsingId(categoryId));
            product.setPrice(price);
            productRepo.save(product);
            return new Response("successfully updated product's name,categoryId and price");
        }

        else if(name != null && categoryId != null){
            if (productRepo.findByName(product.getName()) != null) {
                throw new KeyAlreadyExistsException("Product name already exists.");
            }
            if (!categoryRepo.existsById(categoryId)) {
                throw new KeyAlreadyExistsException("Category name already exists.");
            }
            product.setName(name);
            product.setCategory(categoryRepo.findUsingId(categoryId));
            productRepo.save(product);
            return new Response("successfully updated product's name and categoryId");
        }

        else if(name != null && price != null){
            if (productRepo.findByName(product.getName()) != null) {
                throw new KeyAlreadyExistsException("Product name already exists.");
            }
            product.setName(name);
            product.setPrice(price);
            productRepo.save(product);
            return new Response("successfully updated product's name and price");
        }

        else if(categoryId != null && price != null){
            if (!categoryRepo.existsById(categoryId)) {
                throw new KeyAlreadyExistsException("Category name already exists.");
            }
            product.setCategory(categoryRepo.findUsingId(categoryId));
            product.setPrice(price);
            productRepo.save(product);
            return new Response("successfully updated product's category and price");
        }

        else if(name != null){
            if (productRepo.findByName(product.getName()) != null) {
                throw new KeyAlreadyExistsException("Product name already exists.");
            }
            product.setName(name);
            productRepo.save(product);
            return new Response("successfully updated product's name");
        }

        else if(categoryId != null){
            if (!categoryRepo.existsById(categoryId)) {
                throw new KeyAlreadyExistsException("Category name already exists.");
            }
            product.setCategory(categoryRepo.findUsingId(categoryId));
            productRepo.save(product);
            return new Response("successfully updated product's category");
        }
        else{
            product.setPrice(price);
            productRepo.save(product);
            return new Response("successfully updated product's price");
        }
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
            products = productRepo.findAllUsingId(productId);
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
    

//CATEGORY

    //add category
    public Response addCategory(Category category) {

        //Check if the category name already exists (it should be unique)
        if (categoryRepo.findByName(category.getName()) != null) {
            throw new KeyAlreadyExistsException("Category name already exists.");
        }
        categoryRepo.save(category);
        return new Response("Category added successfully with ID: " + category.getId());
    }

}
