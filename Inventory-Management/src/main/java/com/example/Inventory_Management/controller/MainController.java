package com.example.Inventory_Management.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
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
    @GetMapping("products/get")
    public Map<String, Object> getProduct(
        @RequestParam(required = false) Integer productId,
        @RequestParam(required = false) Integer categoryId) {
        List<Product> products = productService.getProduct(productId,categoryId);
        Map<String, Object> response = new HashMap<>();
        response.put("Products", products);
        return response;
    }

    //Delete a product
    @DeleteMapping("products/delete")
    public Response deleteProduct(@RequestParam Integer productId)
    {
        return productService.deleteProduct(productId);
    }

    //Add a category
    @PostMapping("/categories/add")
    public Response addCategory(@RequestBody Category category) {
        return productService.addCategory(category);
    }

}
