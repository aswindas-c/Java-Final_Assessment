package com.example.Inventory_Management.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.Inventory_Management.DTO.ProductDto;
import com.example.Inventory_Management.DTO.ProductResponseDto;
import com.example.Inventory_Management.DTO.Response;
import com.example.Inventory_Management.model.Category;
import com.example.Inventory_Management.repository.CategoryRepo;
import com.example.Inventory_Management.service.ProductService;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@RequestMapping("/api")
public class MainController {

    private Logger logger = Logger.getLogger(MainController.class);

    @Autowired
    private ProductService productService;

    @Autowired
    CategoryRepo categoryRepo;

    //Add a product
    @PostMapping("/products/add")
    public Response addProduct(@RequestBody ProductDto productDto) {

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        Response response = productService.addProduct(productDto);

        stopWatch.stop();
        logger.info("Add-Product Query executed in " + stopWatch.getTotalTimeMillis() + "ms");
        
        return response;
    }

    //Get products based on product id or category id or both
    @GetMapping("/products/get")
    public Map<String, Object> getProduct(
        @RequestParam(required = false) Integer productId,
        @RequestParam(required = false) Integer categoryId) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        List<ProductResponseDto> products = productService.getProduct(productId,categoryId);
        Map<String, Object> response = new HashMap<>();
        response.put("Products", products);

        stopWatch.stop();
        logger.info("Get-Product Query executed in " + stopWatch.getTotalTimeMillis() + "ms");

        return response;

    }

    //Delete a product
    @DeleteMapping("/products/delete")
    public Response deleteProduct(@RequestParam Integer productId)
    {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        Response response = productService.deleteProduct(productId);

        stopWatch.stop();
        logger.info("Delete-Product Query executed in " + stopWatch.getTotalTimeMillis() + "ms");
        return response;
    }

    //Update products name,categoryId,price
    @PutMapping("/products/update")
    public Response updateProduct(
        @RequestParam Integer productId,
        @RequestParam(required = false) String name,
        @RequestParam(required = false) Integer categoryId,
        @RequestParam(required = false) Double price){
            StopWatch stopWatch = new StopWatch();
            stopWatch.start();
            Response response = productService.updateProduct(productId,name,categoryId,price);
            stopWatch.stop();
            logger.info("Update-Product Query executed in " + stopWatch.getTotalTimeMillis() + "ms");
            return response;
        }

    //Add a category
    @PostMapping("/categories/add")
    public Response addCategory(@RequestBody Category category) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        Response response = productService.addCategory(category);
        stopWatch.stop();
        logger.info("Add-Category Query executed in " + stopWatch.getTotalTimeMillis() + "ms");
        return response;
    }

    //Get all category  or details of a specific category
    @GetMapping("/categories/get")
    public Map<String, Object> getCategory(
        @RequestParam(required = false) Integer categoryId){
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        List<Category> categories = productService.getCategory(categoryId);
        Map<String, Object> response = new HashMap<>();
        response.put("Categories", categories);
        stopWatch.stop();
        logger.info("Get-Category Query executed in " + stopWatch.getTotalTimeMillis() + "ms");
        return response;
    }

    //Update category name
    @PutMapping("/categories/update")
    public Response updateCategory(
        @RequestParam Integer categoryId,
        @RequestParam String name){

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        Response response = productService.updateCategory(categoryId,name);
        stopWatch.stop();
        logger.info("Update-Category Query executed in " + stopWatch.getTotalTimeMillis() + "ms");
        return response;
    }
    
    //Delete a category
    @DeleteMapping("/categories/delete")
    public Response deleteCategory(@RequestParam Integer categoryId)
    {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        Response response = productService.deleteCategory(categoryId);
        stopWatch.stop();
        logger.info("Delete-Category Query executed in " + stopWatch.getTotalTimeMillis() + "ms");
        return response;
    }

    //Sell a product
    @PutMapping("/products/sell")
    public Response sellProduct(
        @RequestParam Integer productId,
        @RequestParam Integer quantity){
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        Response response = productService.sellProduct(productId,quantity);
        stopWatch.stop();
        logger.info("Sell-Product Query executed in " + stopWatch.getTotalTimeMillis() + "ms");
        return response;
    }
    
    //Restock product
    @PutMapping("/products/restock")
    public Response restockProduct(
        @RequestParam Integer productId,
        @RequestParam Integer quantity){
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        Response response = productService.restockProduct(productId,quantity);
        stopWatch.stop();
        logger.info("Restock-Product Query executed in " + stopWatch.getTotalTimeMillis() + "ms");
        return response;
    }

}
