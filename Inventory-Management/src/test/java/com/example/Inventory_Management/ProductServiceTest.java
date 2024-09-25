package com.example.Inventory_Management;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import javax.management.openmbean.KeyAlreadyExistsException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import com.example.Inventory_Management.DTO.ProductDto;
import com.example.Inventory_Management.DTO.Response;
import com.example.Inventory_Management.model.Category;
import com.example.Inventory_Management.model.Product;
import com.example.Inventory_Management.repository.CategoryRepo;
import com.example.Inventory_Management.repository.ProductRepo;
import com.example.Inventory_Management.service.ProductService;

class ProductServiceTest {

    @Mock
    private ProductRepo productRepo;

    @Mock
    private CategoryRepo categoryRepo;

    @InjectMocks
    private ProductService productService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this); // Initialize mocks
    }

    @Test
    void testAddProduct_Success() {
        // Setup
        ProductDto productDto = new ProductDto();
        productDto.setName("TestProduct");
        productDto.setCategoryId(1);
        productDto.setPrice(100.0);
        productDto.setQuantity(10);

        Category category = new Category();
        category.setId(1);
        category.setName("TestCategory");

        Product savedProduct = new Product();
        savedProduct.setId(1);  // Simulate auto-generated ID
        savedProduct.setName("TestProduct");
        savedProduct.setCategory(category);
        savedProduct.setPrice(100.0);
        savedProduct.setQuantity(10);

        when(categoryRepo.findById(1)).thenReturn(Optional.of(category));
        when(productRepo.findByName("TestProduct")).thenReturn(null);
        
        when(productRepo.save(any(Product.class))).thenAnswer(invocation -> {
            Product product = invocation.getArgument(0);
            product.setId(1);  // Simulate the ID being set after save
            return product;
        });

        // Act
        Response response = productService.addProduct(productDto);

        // Assert
        assertEquals("Product added successfully with ID: 1", response.getMessage());  // ID would be null unless mock behavior is adjusted.
        verify(productRepo, times(1)).save(any(Product.class));
    }

    @Test
    void testAddProduct_ProductNameExists() {
        // Setup
        ProductDto productDto = new ProductDto();
        productDto.setName("TestProduct");
        productDto.setCategoryId(1);
        productDto.setPrice(100.0);
        productDto.setQuantity(10);

        Category category = new Category();
        category.setId(1);
        category.setName("TestCategory");

        Product savedProduct = new Product();
        savedProduct.setId(1);  // Simulate auto-generated ID
        savedProduct.setName("TestProduct");
        savedProduct.setCategory(category);
        savedProduct.setPrice(100.0);
        savedProduct.setQuantity(10);

        when(categoryRepo.findById(1)).thenReturn(Optional.of(category));
        when(productRepo.findByName("TestProduct")).thenReturn(savedProduct);

        // Act
        Exception exception = assertThrows(KeyAlreadyExistsException.class, () -> {
            productService.addProduct(productDto);
        });

        // Assert
        assertEquals("Product name already exists.", exception.getMessage());
    }


    @Test
    void testAddProduct_StockLevelNegative() {
        ProductDto productDto = new ProductDto();
        productDto.setQuantity(-1);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            productService.addProduct(productDto);
        });

        assertEquals("Stock levels cannot be negative.", exception.getMessage());
    }

    @Test
    void testAddProduct_PriceNegative() {
        ProductDto productDto = new ProductDto();
        productDto.setQuantity(200);
        productDto.setPrice((double)-1);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            productService.addProduct(productDto);
        });

        assertEquals("Price cannot be negative.", exception.getMessage());
    }

    @Test
    void testAddProduct_CategoryNotFound() {
        ProductDto productDto = new ProductDto();
        productDto.setName("TestProduct");
        productDto.setCategoryId(999);
        productDto.setPrice(100.0);
        productDto.setQuantity(10);

        when(categoryRepo.findById(999)).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> {
            productService.addProduct(productDto);
        });

        assertEquals("Category does not exist", exception.getMessage());
    }

    @Test
    void testUpdateProduct_Success() {
        // Setup
        Product product = new Product();
        product.setId(1);
        product.setName("OldProduct");
        product.setQuantity(5);
        product.setPrice(50.0);

        when(productRepo.findUsingId(1)).thenReturn(product);
        when(productRepo.findByName("NewProduct")).thenReturn(null);

        // Act
        Response response = productService.updateProduct(1, "NewProduct", null, 100.0);

        // Assert
        assertEquals("successfully updated product's name and price", response.getMessage());
        assertEquals("NewProduct", product.getName());
        assertEquals(100.0, product.getPrice());
        verify(productRepo, times(1)).save(product);
    }
    
    // More test cases for other methods like sellProduct, deleteProduct, etc.
}
