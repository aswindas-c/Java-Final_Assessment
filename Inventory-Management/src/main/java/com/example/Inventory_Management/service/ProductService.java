package com.example.Inventory_Management.service;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.ConcurrentHashMap;

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


    private Map<Integer, Product> productCache = new ConcurrentHashMap<>();    
    private Map<Integer, Category> categoryCache = new ConcurrentHashMap<>();

    public Response addProduct(ProductDto productDto) {

        //Stock levels and price cannot be negative.
        if(productDto.getQuantity()<0){
            throw new IllegalArgumentException("Stock levels cannot be negative.");
        }
        if(productDto.getPrice()<0){
            throw new IllegalArgumentException("Price cannot be negative.");
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
        productCache.put(product.getId(), product);
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
            if (productRepo.findByName(name) != null) {
                throw new KeyAlreadyExistsException("Product name already exists.");
            }
            if (!categoryRepo.existsById(categoryId)) {
                throw new KeyAlreadyExistsException("Category Id does not exists.");
            }
            product.setName(name);
            product.setCategory(categoryRepo.findUsingId(categoryId));
            product.setPrice(price);
            productRepo.save(product);
            productCache.put(productId, product);
            return new Response("successfully updated product's name,categoryId and price");
        }

        else if(name != null && categoryId != null){
            if (productRepo.findByName(name) != null) {
                throw new KeyAlreadyExistsException("Product name already exists.");
            }
            if (!categoryRepo.existsById(categoryId)) {
                throw new KeyAlreadyExistsException("Category Id does not exists.");
            }
            product.setName(name);
            product.setCategory(categoryRepo.findUsingId(categoryId));
            productRepo.save(product);
            productCache.put(productId, product);
            return new Response("successfully updated product's name and categoryId");
        }

        else if(name != null && price != null){
            if (productRepo.findByName(name) != null) {
                throw new KeyAlreadyExistsException("Product name already exists.");
            }
            product.setName(name);
            product.setPrice(price);
            productRepo.save(product);
            productCache.put(productId, product);
            return new Response("successfully updated product's name and price");
        }

        else if(categoryId != null && price != null){
            if (!categoryRepo.existsById(categoryId)) {
                throw new KeyAlreadyExistsException("Category Id does not exists.");
            }
            product.setCategory(categoryRepo.findUsingId(categoryId));
            product.setPrice(price);
            productRepo.save(product);
            productCache.put(productId, product);
            return new Response("successfully updated product's category and price");
        }

        else if(name != null){
            if (productRepo.findByName(name) != null) {
                throw new KeyAlreadyExistsException("Product name already exists.");
            }
            product.setName(name);
            productRepo.save(product);
            productCache.put(productId, product);
            return new Response("successfully updated product's name");
        }

        else if(categoryId != null){
            if (!categoryRepo.existsById(categoryId)) {
                throw new KeyAlreadyExistsException("Category Id does not exists.");
            }
            product.setCategory(categoryRepo.findUsingId(categoryId));
            productRepo.save(product);
            productCache.put(productId, product);
            return new Response("successfully updated product's category");
        }
        else if(price != null){
            product.setPrice(price);
            productRepo.save(product);
            productCache.put(productId, product);
            return new Response("successfully updated product's price");
        }
        else{
            return new Response("Enter name,categoryId or price to be updated");
        }
    }

    //Get products based on product id or category id or both
    public List<Product> getProduct(Integer productId, Integer categoryId) {
        List<Product> products;
        if (productId != null && categoryId != null) {
            if(productCache.containsKey(productId)) 
            {        
                Product cachedProduct = productCache.get(productId);                
                    return List.of(cachedProduct); 
            }
            products = productRepo.findByCategoryIdandId(productId,categoryId);
            if(products.isEmpty())
            {
                throw new NoSuchElementException("No Product exists under that category id and product id");
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
            if(productCache.containsKey(productId)) 
            {        
                Product cachedProduct = productCache.get(productId);                
                    return List.of(cachedProduct); 
            }
            products = productRepo.findAllUsingId(productId);
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

    public Response deleteProduct(Integer productId) {
        Product product = productRepo.findUsingId(productId);
        if(product == null)
        {
            throw new NoSuchElementException("Product with id "+productId+"doesnt exist!!");
        }
        productRepo.delete(product);
        productCache.remove(productId);
        return new Response("Successfully deleted product with id "+productId);
    }
    

//CATEGORY


    //add category
    public Response addCategory(Category category) {

        //Check if the category name already exists (it should be unique)
        if (categoryRepo.findByName(category.getName()) != null) {
            throw new KeyAlreadyExistsException("Category name already exists.");
        }
        categoryRepo.save(category);
        categoryCache.put(category.getId(), category);
        return new Response("Category added successfully with ID: " + category.getId());
    }

    //Get all category  or details of a specific category
    public List<Category> getCategory(Integer categoryId) {
        List<Category> categories;
        if (categoryId != null) {
            if(categoryCache.containsKey(categoryId)) 
            {        
                Category cachedCategory = categoryCache.get(categoryId);                
                    return List.of(cachedCategory); 
            }
            categories = categoryRepo.findAllUsingId(categoryId);
            if(categories.isEmpty())
            {
                throw new NoSuchElementException("No category with that id found");
            }
            return categories;
        } 
        else 
        {
            if(categoryRepo.findAll().isEmpty())
            {
                throw new NoSuchElementException("No categories exist.");
            }
            return categoryRepo.findAll();
        }
    }

    //updateCategory
    @Transactional
    public Response updateCategory(Integer categoryId, String name) {

        Category category = categoryRepo.findUsingId(categoryId);
        if(category == null){
            throw new NoSuchElementException("Cateogry with given id does not exist");
        }

        if (categoryRepo.findByName(name) != null) {
            throw new KeyAlreadyExistsException("Category name already exists.");
        }
        category.setName(name);
        categoryRepo.save(category);
        categoryCache.put(category.getId(), category);
        return new Response("successfully updated category's name");
    
    }
    //deleteProduct
    public Response deleteCategory(Integer categoryId) {
        Category category = categoryRepo.findUsingId(categoryId);
        if(category == null)
        {
            throw new NoSuchElementException("category with id "+ categoryId+" doesnt exist!!");
        }

        List<Product> products = productRepo.findByCategoryId(categoryId); 
        if(!products.isEmpty())
        {
            throw new IllegalStateException("Category cannot be deleted.There are products in that category");
        }
        categoryRepo.delete(category);
        categoryCache.remove(categoryId);
        return new Response("Successfully deleted cateogry with id " + categoryId);
    }

    //Sell a product
    public Response sellProduct(Integer productId, Integer quantity) {

        Product product = productRepo.findUsingId(productId);
        if(product == null){
            throw new NoSuchElementException("Product with given id does not exist");
        }
        if(quantity<=0)
        {
            throw new IllegalArgumentException("Enter valid quantity");
        }
        if(product.getQuantity()<quantity)
        {
            throw new IllegalArgumentException("Required quantity not available.Available quantity = "+product.getQuantity());
        }
        product.setQuantity(product.getQuantity()-quantity);
        productRepo.save(product);
        productCache.put(productId, product);
        return new Response(quantity+" products sold. Remaining quantity = "+product.getQuantity());
    
    }

    //Restock a product
    public Response restockProduct(Integer productId, Integer quantity) {

        Product product = productRepo.findUsingId(productId);
        if(product == null){
            throw new NoSuchElementException("Product with given id does not exist");
        }
        if(quantity<=0)
        {
            throw new IllegalArgumentException("Enter valid quantity");
        }
        product.setQuantity(product.getQuantity()+quantity);
        productRepo.save(product);
        productCache.put(productId, product);
        return new Response(quantity+" products restocked. Updated quantity = "+product.getQuantity());
    }
}
