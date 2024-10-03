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

import com.example.Inventory_Management.DTO.Response;
import com.example.Inventory_Management.controller.MainController;
import com.example.Inventory_Management.repository.CategoryRepo;
import com.example.Inventory_Management.service.ProductService;

import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


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
        when(productService.deleteCategory(2)).thenReturn(new Response("Successfully deleted cateogry with id 2"));
        try {
            mvc.perform(MockMvcRequestBuilders.delete("/api/categories/delete")
                    .param("categoryId", "2")
                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message", equalTo("Successfully deleted cateogry with id 2")));
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