package com.example.Inventory_Management.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.Inventory_Management.DTO.ProductDto;
import com.example.Inventory_Management.DTO.Response;
import com.example.Inventory_Management.model.Category;
import com.example.Inventory_Management.model.Product;
import com.example.Inventory_Management.repository.CategoryRepo;
import com.example.Inventory_Management.service.ProductService;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@RequestMapping("/api")
public class MainController {

    @Autowired
    private ProductService productService;

    @Autowired
    CategoryRepo categoryRepo;

    //Add a product
    @PostMapping("/products/add")
    public Response addProduct(@RequestBody ProductDto productDto) {
        return productService.addProduct(productDto);
    }

    //Get products based on product id or category id or both
    @GetMapping("/products/get")
    public Map<String, Object> getProduct(
        @RequestParam(required = false) Integer productId,
        @RequestParam(required = false) Integer categoryId) {
        List<Product> products = productService.getProduct(productId,categoryId);
        Map<String, Object> response = new HashMap<>();
        response.put("Products", products);
        return response;
    }

    //Delete a product
    @DeleteMapping("/products/delete")
    public Response deleteProduct(@RequestParam Integer productId)
    {
        return productService.deleteProduct(productId);
    }

    //Update products name,categoryId,price
    @PutMapping("/products/update")
    public Response updateProduct(
        @RequestParam Integer productId,
        @RequestParam(required = false) String name,
        @RequestParam(required = false) Integer categoryId,
        @RequestParam(required = false) Double price){
            return productService.updateProduct(productId,name,categoryId,price);
        }

    //Add a category
    @PostMapping("/categories/add")
    public Response addCategory(@RequestBody Category category) {
        return productService.addCategory(category);
    }

    //Get all category  or details of a specific category
    @GetMapping("/categories/get")
    public Map<String, Object> getCategory(
        @RequestParam(required = false) Integer categoryId){
        List<Category> categories = productService.getCategory(categoryId);
        Map<String, Object> response = new HashMap<>();
        response.put("Categories", categories);
        return response;
    }

    //Update category name
    @PutMapping("/categories/update")
    public Response updateCategory(
        @RequestParam Integer categoryId,
        @RequestParam String name){
        return productService.updateCategory(categoryId,name);
        }
    
    //Delete a category
    @DeleteMapping("/categories/delete")
    public Response deleteCategory(@RequestParam Integer categoryId)
    {
        return productService.deleteCategory(categoryId);
    }

}
