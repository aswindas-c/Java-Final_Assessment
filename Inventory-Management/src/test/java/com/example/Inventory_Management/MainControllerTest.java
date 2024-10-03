package com.example.Inventory_Management;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.example.Inventory_Management.DTO.ProductDto;
import com.example.Inventory_Management.DTO.ProductResponseDto;
import com.example.Inventory_Management.DTO.Response;
import com.example.Inventory_Management.controller.MainController;
import com.example.Inventory_Management.model.Category;
import com.example.Inventory_Management.repository.CategoryRepo;
import com.example.Inventory_Management.service.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.List;


@WebMvcTest (MainController.class)
@ExtendWith (MockitoExtension.class)
@AutoConfigureMockMvc
class MainControllerTest{

    @Autowired
    MockMvc mvc;

    @MockBean
    ProductService productService;

    @MockBean
    CategoryRepo categoryRepo;

    private ObjectMapper objectMapper = new ObjectMapper();

    //Add a product
    @Test
    void testAdd_Product() {
        ProductDto productDto = new ProductDto("new mobile", 2000, 1000.00, 1);
        when(productService.addProduct(productDto)).thenReturn(new Response("Product added successfully"));
        try {
            mvc.perform(MockMvcRequestBuilders.post("/api/products/add")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(productDto)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message", equalTo("Product added successfully")));
            
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    //Get Products
    @Test
    void testGet_Product() {
        ProductResponseDto product1 = new ProductResponseDto(2,"mobile",2000,1000.00,1);
        List<ProductResponseDto> productList = Arrays.asList(product1);
        when(productService.getProduct(2,1)).thenReturn(productList);
        try {
            mvc.perform(MockMvcRequestBuilders.get("/api/products/get")
                    .param("productId", "2")
                    .param("categoryId", "1")
                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.Products", hasSize(1)))
                    .andExpect(jsonPath("$.Products[0].name", equalTo("mobile")));
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    //Delete Product
    @Test
    void testDelete_Product() {
        when(productService.deleteProduct(2)).thenReturn(new Response("Successfully deleted product with id 2"));
        try {
            mvc.perform(MockMvcRequestBuilders.delete("/api/products/delete")
                    .param("productId", "2")
                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message", equalTo("Successfully deleted product with id 2")));
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    //Update Product
    @Test
    void testPut_Update_Product() {
        when(productService.updateProduct(2,"Cars",1,2000.00)).thenReturn(new Response("successfully updated product's name,categoryId and price"));
        try {
            mvc.perform(MockMvcRequestBuilders.put("/api/products/update")
                    .param("productId", "2")
                    .param("name","Cars")
                    .param("categoryId", "1")
                    .param("price", "2000.00")
                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message", equalTo("successfully updated product's name,categoryId and price")));
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    //Add a category
    @Test
    void testAdd_Category() {
        Category category = new Category(2,"new mobile");
        when(productService.addCategory(category)).thenReturn(new Response("Category added successfully"));
        try {
            mvc.perform(MockMvcRequestBuilders.post("/api/categories/add")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(category)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message", equalTo("Category added successfully")));
            
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    //Get Category
    @Test
    void testGet_Category() {
        Category category1 = new Category();
        category1.setName("Toys");
        Category category2 = new Category();
        category2.setName("Electronics");
        
        List<Category> categoryList = Arrays.asList(category1,category2);
        when(productService.getCategory(2)).thenReturn(categoryList);
        try {
            mvc.perform(MockMvcRequestBuilders.get("/api/categories/get")
                    .param("categoryId", "2")
                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.Categories", hasSize(2)))
                    .andExpect(jsonPath("$.Categories[0].name", equalTo("Toys")))
                    .andExpect(jsonPath("$.Categories[1].name", equalTo("Electronics")));
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    //Update Category
    @Test
    void testPut_Update_Category() {
        when(productService.updateCategory(2,"Cars")).thenReturn(new Response("successfully updated category's name"));
        try {
            mvc.perform(MockMvcRequestBuilders.put("/api/categories/update")
                    .param("categoryId", "2")
                    .param("name","Cars")
                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message", equalTo("successfully updated category's name")));
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    //Delete Category
    @Test
    void testDelete_Category() {
        when(productService.deleteCategory(2)).thenReturn(new Response("Successfully deleted category with id 2"));
        try {
            mvc.perform(MockMvcRequestBuilders.delete("/api/categories/delete")
                    .param("categoryId", "2")
                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message", equalTo("Successfully deleted category with id 2")));
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }
    //Sell Products
    @Test
    void testPut_Sell_Product() {
        when(productService.sellProduct(2,100)).thenReturn(new Response("100 products sold. Remaining quantity = 300"));
        try {
            mvc.perform(MockMvcRequestBuilders.put("/api/products/sell")
                    .param("productId", "2")
                    .param("quantity", "100")
                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message", equalTo("100 products sold. Remaining quantity = 300")));
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    //Restock Products
    @Test
    void testPut_Restock_Product() {
        when(productService.restockProduct(2,100)).thenReturn(new Response("100 products restocked. Updated quantity = 300"));
        try {
            mvc.perform(MockMvcRequestBuilders.put("/api/products/restock")
                    .param("productId", "2")
                    .param("quantity", "100")
                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message", equalTo("100 products restocked. Updated quantity = 300")));
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }
}