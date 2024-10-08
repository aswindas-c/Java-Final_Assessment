package com.example.Inventory_Management;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import javax.management.openmbean.KeyAlreadyExistsException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import com.example.Inventory_Management.DTO.ProductDto;
import com.example.Inventory_Management.DTO.ProductResponseDto;
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

    @Mock
    private Map<Integer, Product> productCache;

    @Mock
    private Map<Integer, Category> categoryCache; 

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this); // Initialize mocks
    }

    //add product success
    @Test
    void testAddProduct_Success() {
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
        Response response = productService.addProduct(productDto);

        assertEquals("Product added successfully with ID: 1", response.getMessage());  // ID would be null unless mock behavior is adjusted.
        verify(productRepo, times(1)).save(any(Product.class));
    }

    //add product - product name exists
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

        Exception exception = assertThrows(KeyAlreadyExistsException.class, () -> {
            productService.addProduct(productDto);
        });

        assertEquals("Product name already exists.", exception.getMessage());
    }

    //add product - quantity neagtive
    @Test
    void testAddProduct_StockLevelNegative() {
        ProductDto productDto = new ProductDto();
        productDto.setQuantity(-1);
        productDto.setName("Pen");
        productDto.setCategoryId(3);
        productDto.setPrice(100.00);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            productService.addProduct(productDto);
        });

        assertEquals("Stock levels cannot be negative.", exception.getMessage());
    }

    //add product - price negative
    @Test
    void testAddProduct_PriceNegative() {
        ProductDto productDto = new ProductDto();
        productDto.setQuantity(200);
        productDto.setPrice((double)-1);
        productDto.setName("Pen");
        productDto.setCategoryId(3);


        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            productService.addProduct(productDto);
        });

        assertEquals("Price cannot be negative.", exception.getMessage());
    }
    
    //add product - category not found
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

    //update product - success
    @Test
    void testUpdateProduct_Success() {
        Product product = new Product();
        product.setId(1);
        product.setName("OldProduct");
        product.setQuantity(5);
        product.setPrice(50.0);

        when(productRepo.findUsingId(1)).thenReturn(product);
        when(productRepo.findByName("NewProduct")).thenReturn(null);

        Response response = productService.updateProduct(1, "NewProduct", null, 100.0);

        assertEquals("successfully updated product's name and price", response.getMessage());
        assertEquals("NewProduct", product.getName());
        assertEquals(100.0, product.getPrice());
        verify(productRepo, times(1)).save(product);
    }
    
    //update product - success - all fields
    @Test
    void testUpdateProduct_Success_AllFields() {
        Integer productId = 1;
        String newName = "UpdatedProduct";
        Integer newCategoryId = 2;
        Double newPrice = 200.0;

        Product existingProduct = new Product();
        existingProduct.setId(productId);
        existingProduct.setName("OldProduct");
        existingProduct.setCategory(new Category(1, "OldCategory"));
        existingProduct.setPrice(100.0);

        Category newCategory = new Category();
        newCategory.setId(newCategoryId);
        newCategory.setName("NewCategory");

        when(productRepo.findUsingId(productId)).thenReturn(existingProduct);
        when(productRepo.findByName(newName)).thenReturn(null);
        when(categoryRepo.existsById(newCategoryId)).thenReturn(true);
        when(categoryRepo.findUsingId(newCategoryId)).thenReturn(newCategory);

        Response response = productService.updateProduct(productId, newName, newCategoryId, newPrice);

        assertEquals("successfully updated product's name,categoryId and price", response.getMessage());
        verify(productRepo, times(1)).save(existingProduct);
        verify(productCache, times(1)).put(productId, existingProduct);
    }

    //update product - failure - product not found
    @Test
    void testUpdateProduct_Failure_ProductNotFound() {
        Integer productId = 1;
        String newName = "UpdatedProduct";
        Integer newCategoryId = 2;
        Double newPrice = 200.0;

        when(productRepo.findUsingId(productId)).thenReturn(null); 

        Exception exception = assertThrows(NoSuchElementException.class, () -> {
            productService.updateProduct(productId, newName, newCategoryId, newPrice);
        });
        assertEquals("Product with given id does not exist", exception.getMessage());
    }

    //update product - failure - all fields - product name exist
    @Test
    void testUpdateProduct_Failure_ProductNameExists() {
        Integer productId = 1;
        String newName = "UpdatedProduct";
        Integer newCategoryId = 2;
        Double newPrice = 200.0;

        Product existingProduct = new Product();
        existingProduct.setId(productId);
        existingProduct.setName("OldProduct");
        existingProduct.setCategory(new Category(1, "OldCategory"));
        existingProduct.setPrice(100.0);

        when(productRepo.findUsingId(productId)).thenReturn(existingProduct);
        when(productRepo.findByName(newName)).thenReturn(new Product()); 

        Exception exception = assertThrows(KeyAlreadyExistsException.class, () -> {
            productService.updateProduct(productId, newName, newCategoryId, newPrice);
        });
        assertEquals("Product name already exists.", exception.getMessage());
    }

    //update product - failure - all fields - category not found
    @Test
    void testUpdateProduct_Failure_CategoryNotFound() {
        Integer productId = 1;
        String newName = "UpdatedProduct";
        Integer newCategoryId = 2;
        Double newPrice = 200.0;

        Product existingProduct = new Product();
        existingProduct.setId(productId);
        existingProduct.setName("OldProduct");
        existingProduct.setCategory(new Category(1, "OldCategory"));
        existingProduct.setPrice(100.0);

        when(productRepo.findUsingId(productId)).thenReturn(existingProduct);
        when(productRepo.findByName(newName)).thenReturn(null);
        when(categoryRepo.existsById(newCategoryId)).thenReturn(false); 
        Exception exception = assertThrows(KeyAlreadyExistsException.class, () -> {
            productService.updateProduct(productId, newName, newCategoryId, newPrice);
        });
        assertEquals("Category Id does not exists.", exception.getMessage());
    }

    //update product - failure - all fields - price is negative
    @Test
    void testUpdateProduct_Failure_PriceNegative() {
        Integer productId = 1;
        String newName = "UpdatedProduct";
        Integer newCategoryId = 2;
        Double newPrice = -45.0;

        Product existingProduct = new Product();
        existingProduct.setId(productId);
        existingProduct.setName("OldProduct");
        existingProduct.setCategory(new Category(1, "OldCategory"));
        existingProduct.setPrice(100.0);

        when(productRepo.findUsingId(productId)).thenReturn(existingProduct);
        when(productRepo.findByName(newName)).thenReturn(null);
        when(categoryRepo.existsById(newCategoryId)).thenReturn(true);


        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            productService.updateProduct(productId, newName, newCategoryId, newPrice);
        });
        assertEquals("Price must be positive.", exception.getMessage());
    }


    //update product - success - price only
    @Test
    void testUpdateProduct_Success_PriceOnly() {
        Integer productId = 1;
        Double newPrice = 200.0;

        Product existingProduct = new Product();
        existingProduct.setId(productId);
        existingProduct.setName("OldProduct");
        existingProduct.setCategory(new Category(1, "OldCategory"));
        existingProduct.setPrice(100.0);

        when(productRepo.findUsingId(productId)).thenReturn(existingProduct);

        Response response = productService.updateProduct(productId, null, null, newPrice);

        assertEquals("successfully updated product's price", response.getMessage());
        verify(productRepo, times(1)).save(existingProduct);
        verify(productCache, times(1)).put(productId, existingProduct);
    }

    //update product - failure - price only - price negative
    @Test
    void testUpdateProduct_Success_PriceNegative() {
        Integer productId = 1;
        Double newPrice = -200.0;

        Product existingProduct = new Product();
        existingProduct.setId(productId);
        existingProduct.setName("OldProduct");
        existingProduct.setCategory(new Category(1, "OldCategory"));
        existingProduct.setPrice(100.0);

        when(productRepo.findUsingId(productId)).thenReturn(existingProduct);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            productService.updateProduct(productId,null, null, newPrice);
        });

        assertEquals("Price must be positive.", exception.getMessage());
    }

    //update product - success - Category only
    @Test
    void testUpdateProduct_Success_CategoryOnly() {
        Integer productId = 1;
        Integer newCategoryId = 2;

        Product existingProduct = new Product();
        existingProduct.setId(productId);
        existingProduct.setName("OldProduct");
        existingProduct.setCategory(new Category(1, "OldCategory"));

        Category newCategory = new Category();
        newCategory.setId(newCategoryId);
        newCategory.setName("NewCategory");

        when(productRepo.findUsingId(productId)).thenReturn(existingProduct);
        when(categoryRepo.existsById(newCategoryId)).thenReturn(true);
        when(categoryRepo.findUsingId(newCategoryId)).thenReturn(newCategory);

        Response response = productService.updateProduct(productId, null, newCategoryId, null);

        assertEquals("successfully updated product's category", response.getMessage());
        verify(productRepo, times(1)).save(existingProduct);
        verify(productCache, times(1)).put(productId, existingProduct);
    }

    //update product - failure - category only - category not found
    @Test
    void testUpdateProduct_Failure_CategoryOnly_CategoryNotFound() {
        Integer productId = 1;
        Integer newCategoryId = 299;

        Product existingProduct = new Product();
        existingProduct.setId(productId);
        existingProduct.setName("OldProduct");
        existingProduct.setCategory(new Category(1, "OldCategory"));
        existingProduct.setPrice(100.0);

        when(productRepo.findUsingId(productId)).thenReturn(existingProduct);
        when(categoryRepo.existsById(newCategoryId)).thenReturn(false);

        Exception exception = assertThrows(KeyAlreadyExistsException.class, () -> {
            productService.updateProduct(productId, null,newCategoryId,null);
        });
        assertEquals("Category Id does not exists.", exception.getMessage());
    }


    //update product - success - name only
    @Test
    void testUpdateProduct_Success_NameOnly() {
        Integer productId = 1;
        String newName = "UpdatedProduct";

        Product existingProduct = new Product();
        existingProduct.setId(productId);
        existingProduct.setName("OldProduct");
        existingProduct.setCategory(new Category(1, "OldCategory"));

        Category newCategory = new Category();
        newCategory.setName("NewCategory");

        when(productRepo.findUsingId(productId)).thenReturn(existingProduct);
        when(productRepo.findByName(newName)).thenReturn(null);

        Response response = productService.updateProduct(productId, newName, null, null);

        assertEquals("successfully updated product's name", response.getMessage());
        verify(productRepo, times(1)).save(existingProduct);
        verify(productCache, times(1)).put(productId, existingProduct);
    }

    //update product - failure - name only - product name exist
    @Test
    void testUpdateProduct_Failure_NameOnly_ProductNameExists() {
        Integer productId = 1;
        String newName = "UpdatedProduct";

        Product existingProduct = new Product();
        existingProduct.setId(productId);
        existingProduct.setName("OldProduct");
        existingProduct.setCategory(new Category(1, "OldCategory"));
        existingProduct.setPrice(100.0);

        when(productRepo.findUsingId(productId)).thenReturn(existingProduct);
        when(productRepo.findByName(newName)).thenReturn(new Product()); 

        Exception exception = assertThrows(KeyAlreadyExistsException.class, () -> {
            productService.updateProduct(productId, newName, null, null);
        });
        assertEquals("Product name already exists.", exception.getMessage());
    }

    //update product - success - name and category
    @Test
    void testUpdateProduct_Success_NameAndCategory() {
        Integer productId = 1;
        String newName = "UpdatedProduct";
        Integer newCategoryId = 2;

        Product existingProduct = new Product();
        existingProduct.setId(productId);
        existingProduct.setName("OldProduct");
        existingProduct.setCategory(new Category(1, "OldCategory"));

        Category newCategory = new Category();
        newCategory.setId(newCategoryId);
        newCategory.setName("NewCategory");

        when(productRepo.findUsingId(productId)).thenReturn(existingProduct);
        when(productRepo.findByName(newName)).thenReturn(null);
        when(categoryRepo.existsById(newCategoryId)).thenReturn(true);
        when(categoryRepo.findUsingId(newCategoryId)).thenReturn(newCategory);

        Response response = productService.updateProduct(productId, newName, newCategoryId, null);

        assertEquals("successfully updated product's name and categoryId", response.getMessage());
        verify(productRepo, times(1)).save(existingProduct);
        verify(productCache, times(1)).put(productId, existingProduct);
    }

    

    //update product - failure - name and category - product name exist
    @Test
    void testUpdateProduct_Failure_NameAndCategory_ProductNameExists() {
        Integer productId = 1;
        String newName = "UpdatedProduct";
        Integer newCategoryId = 2;

        Product existingProduct = new Product();
        existingProduct.setId(productId);
        existingProduct.setName("OldProduct");
        existingProduct.setCategory(new Category(1, "OldCategory"));

        when(productRepo.findUsingId(productId)).thenReturn(existingProduct);
        when(productRepo.findByName(newName)).thenReturn(new Product());  // Simulate duplicate product name

        Exception exception = assertThrows(KeyAlreadyExistsException.class, () -> {
            productService.updateProduct(productId, newName, newCategoryId, null);
        });
        assertEquals("Product name already exists.", exception.getMessage());
    }
    
    //update product - failure - name and category - category does not exist
    @Test
    void testUpdateProduct_Failure_NameAndCategory_CategoryIdDoesNotExist() {
        Integer productId = 1;
        String newName = "UpdatedProduct";
        Integer newCategoryId = 999; 

        Product existingProduct = new Product();
        existingProduct.setId(productId);
        existingProduct.setName("OldProduct");
        existingProduct.setCategory(new Category(1, "OldCategory"));

        when(productRepo.findUsingId(productId)).thenReturn(existingProduct);
        when(productRepo.findByName(newName)).thenReturn(null);  // No duplicate name
        when(categoryRepo.existsById(newCategoryId)).thenReturn(false);  // Simulate non-existent category

        Exception exception = assertThrows(KeyAlreadyExistsException.class, () -> {
            productService.updateProduct(productId, newName, newCategoryId, null);
        });
        assertEquals("Category Id does not exists.", exception.getMessage());
    }
    
    //update product - sucess - name and price 
    @Test
    void testUpdateProduct_sucesss_NameAndPrice() {
        Integer productId = 1;
        String newName = "UpdatedProduct";
        Double newPrice = 200.0;

        Product existingProduct = new Product();
        existingProduct.setId(productId);
        existingProduct.setName("OldProduct");
        existingProduct.setCategory(new Category(1, "OldCategory"));
        existingProduct.setPrice(100.0);

        when(productRepo.findUsingId(productId)).thenReturn(existingProduct);
        when(productRepo.findByName(newName)).thenReturn(null);

        Response response = productService.updateProduct(productId, newName, null, newPrice);

        assertEquals("successfully updated product's name and price", response.getMessage());
        verify(productRepo, times(1)).save(existingProduct);
        verify(productCache, times(1)).put(productId, existingProduct);
    }

    //update product - failure - name and price - product name exist
    @Test
    void testUpdateProduct_Failure_NameAndPrice_ProductNameExists() {
        Integer productId = 1;
        String newName = "UpdatedProduct";
        Double newPrice = 200.0;
        

        Product existingProduct = new Product();
        existingProduct.setId(productId);
        existingProduct.setName("OldProduct");
        existingProduct.setPrice(100.0);

        when(productRepo.findUsingId(productId)).thenReturn(existingProduct);
        when(productRepo.findByName(newName)).thenReturn(new Product());  // Simulate duplicate product name

        Exception exception = assertThrows(KeyAlreadyExistsException.class, () -> {
            productService.updateProduct(productId, newName, null, newPrice);
        });
        assertEquals("Product name already exists.", exception.getMessage());
    }

     //update product - failure - name and price - price is negative
     @Test
     void testUpdateProduct_Failure_NameAndPrice_PriceNegative() {
         Integer productId = 1;
         String newName = "UpdatedProduct";
         Integer newCategoryId = 2;
         Double newPrice = -45.0;
 
         Product existingProduct = new Product();
         existingProduct.setId(productId);
         existingProduct.setName("OldProduct");
         existingProduct.setCategory(new Category(1, "OldCategory"));
         existingProduct.setPrice(100.0);
 
         when(productRepo.findUsingId(productId)).thenReturn(existingProduct);
         when(productRepo.findByName(newName)).thenReturn(null);
         when(categoryRepo.existsById(newCategoryId)).thenReturn(true);
 
 
         Exception exception = assertThrows(IllegalArgumentException.class, () -> {
             productService.updateProduct(productId, newName, null, newPrice);
         });
         assertEquals("Price must be positive.", exception.getMessage());
     }

     //update product - success - price and category
    @Test
    void testUpdateProduct_Success_PriceAndCategory() {
        Integer productId = 1;
        String newName = "UpdatedProduct";
        Integer newCategoryId = 2; 
        Double newPrice = 200.0;

        Product existingProduct = new Product();
        existingProduct.setId(productId);
        existingProduct.setName("OldProduct");
        existingProduct.setCategory(new Category(1, "OldCategory"));

        Category newCategory = new Category();
        newCategory.setId(newCategoryId);
        newCategory.setName("NewCategory");

        when(productRepo.findUsingId(productId)).thenReturn(existingProduct);
        when(productRepo.findByName(newName)).thenReturn(null);
        when(categoryRepo.existsById(newCategoryId)).thenReturn(true);
        when(categoryRepo.findUsingId(newCategoryId)).thenReturn(newCategory);

        Response response = productService.updateProduct(productId, null, newCategoryId, newPrice);

        assertEquals("successfully updated product's category and price", response.getMessage());
        verify(productRepo, times(1)).save(existingProduct);
        verify(productCache, times(1)).put(productId, existingProduct);
    }

    //update product - failure - price and category - price negative
    @Test
    void testUpdateProduct_Failure_PriceAndCategoryPriceNegative() {
        Integer productId = 1;
        Double newPrice = -200.0;
        Integer newCategoryId = 2; 

        Product existingProduct = new Product();
        existingProduct.setId(productId);
        existingProduct.setName("OldProduct");
        existingProduct.setCategory(new Category(1, "OldCategory"));
        existingProduct.setPrice(100.0);

        when(productRepo.findUsingId(productId)).thenReturn(existingProduct);
        when(categoryRepo.existsById(newCategoryId)).thenReturn(true); 

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            productService.updateProduct(productId,null, newCategoryId, newPrice);
        });

        assertEquals("Price must be positive.", exception.getMessage());
    }

    //update product - failure - price and category - category not found
    @Test
    void testUpdateProduct_Failure_PriceAndCategory_CategoryIdDoesNotExist() {
        Integer productId = 1;
        Double newPrice = -200.0;
        Integer newCategoryId = 999; 

        Product existingProduct = new Product();
        existingProduct.setId(productId);
        existingProduct.setName("OldProduct");
        existingProduct.setCategory(new Category(1, "OldCategory"));

        when(productRepo.findUsingId(productId)).thenReturn(existingProduct);
        when(categoryRepo.existsById(newCategoryId)).thenReturn(false); 
        Exception exception = assertThrows(KeyAlreadyExistsException.class, () -> {
            productService.updateProduct(productId, null, newCategoryId, newPrice);
        });
        assertEquals("Category Id does not exists.", exception.getMessage());
    }

    //update product - failure - nothing given to update
    @Test
    void testUpdateProduct_Failure_NO_UpdateData() {
        Integer productId = 1;

        Product existingProduct = new Product();
        existingProduct.setId(productId);
        existingProduct.setName("OldProduct");
        existingProduct.setCategory(new Category(1, "OldCategory"));

        when(productRepo.findUsingId(productId)).thenReturn(existingProduct);
        Response response = productService.updateProduct(productId, null, null, null);

        assertEquals("Enter name,categoryId or price to be updated", response.getMessage());
    }

    
    //Test for getProduct method

    //existing product - by id
    @Test
    void testGetProduct_ExistingProduct_ById() {
        Integer productId = 1;
        Product existingProduct = new Product();
        existingProduct.setId(productId);
        existingProduct.setName("ExistingProduct");
        existingProduct.setCategory(new Category(1, "ExistingCategory"));

        when(productRepo.findAllUsingId(productId)).thenReturn(List.of(existingProduct));
        when(productCache.containsKey(productId)).thenReturn(true);
        when(productCache.get(productId)).thenReturn(existingProduct);

        List<ProductResponseDto> result = productService.getProduct(productId, null);
        assertEquals(1, result.size());
        assertEquals(existingProduct.getName(), result.get(0).getName());
    }

     //existing product - by category id
    @Test
    void testGetProduct_ExistingProduct_ByCategoryId() {
        Integer categoryId = 1;
        Product existingProduct = new Product();
        existingProduct.setId(1);
        existingProduct.setName("ExistingProduct");
        existingProduct.setCategory(new Category(categoryId, "ExistingCategory"));

        when(productRepo.findByCategoryId(categoryId)).thenReturn(List.of(existingProduct));

        List<ProductResponseDto> result = productService.getProduct(null, categoryId);
        assertEquals(1, result.size());
        assertEquals(existingProduct.getName(), result.get(0).getName());
    }

     //existing product - by both
    @Test
    void testGetProduct_ExistingProduct_ByBoth() {
        Integer productId = 1;
        Integer categoryId = 1;
        Product existingProduct = new Product();
        existingProduct.setId(productId);
        existingProduct.setName("ExistingProduct");
        existingProduct.setCategory(new Category(categoryId, "ExistingCategory"));

        when(productCache.containsKey(productId)).thenReturn(true);
        when (productCache.get(productId)).thenReturn(existingProduct);
        when(productRepo.findByCategoryIdandId(productId, categoryId)).thenReturn(List.of(existingProduct));

        List<ProductResponseDto> result = productService.getProduct(productId, categoryId);
        assertEquals(1, result.size());
        assertEquals(existingProduct.getName(), result.get(0).getName());
    }

     //non-existing product - by id
    @Test
    void testGetProduct_NonExistingProduct_ById() {
        Integer productId = 1;

        when(productRepo.findAllUsingId(productId)).thenReturn(Collections.emptyList());

        Exception exception = assertThrows(NoSuchElementException.class, () -> {
            productService.getProduct(productId, null);
        });
        assertEquals("No Product exists with that product id.", exception.getMessage());
    }

     //non-existing product - by category id
    @Test
    void testGetProduct_NonExistingProduct_ByCategoryId() {
        Integer categoryId = 1;

        when(productRepo.findByCategoryId(categoryId)).thenReturn(Collections.emptyList());

        Exception exception = assertThrows(NoSuchElementException.class, () -> {
            productService.getProduct(null, categoryId);
        });
        assertEquals("No Product exists under that category id.", exception.getMessage());
    }

    //non-existing product - by both
    @Test
    void testGetProduct_NonExistingProduct_ByBoth() {
        Integer productId = 1;
        Integer categoryId = 1;

        List<Product> products = new ArrayList<>();
        when(productRepo.findByCategoryIdandId(productId, categoryId)).thenReturn(products);

        Exception exception = assertThrows(NoSuchElementException.class, () -> {
            productService.getProduct(productId, categoryId);
        });
        assertEquals("No Product exists under that category id and product id", exception.getMessage());
    }

    //no fields given - no products found
    @Test
    void testGetProduct_NoProductsFound() {
        when(productRepo.findAll()).thenReturn(Collections.emptyList());

        Exception exception = assertThrows(NoSuchElementException.class, () -> {
            productService.getProduct(null, null);
        });
        assertEquals("No Products found.", exception.getMessage());
    }

    //delete product

    @Test
    void testDeleteProduct_ExistingProduct() {

        Integer productId = 1;
        Product existingProduct = new Product();
        existingProduct.setId(productId);
        existingProduct.setName("Mobile");

        when(productRepo.findUsingId(productId)).thenReturn(existingProduct);

        Response result = productService.deleteProduct(productId);
        assertEquals("Successfully deleted product with id " + productId, result.getMessage());
    }

    @Test
    void testDeleteProduct_ProductDoesNotExist() {
        Integer productId = 1;

        when(productRepo.findUsingId(productId)).thenReturn(null);

        Exception exception = assertThrows(NoSuchElementException.class, () -> {
            productService.deleteProduct(productId);
        });
        assertEquals("Product with id " + productId + "doesnt exist!!", exception.getMessage());
    }

//CATEGORY

    //add category
    @Test
    void testAddCategory_NewCategory() {
        Category newCategory = new Category();
        newCategory.setName("NewCategory");
        newCategory.setId(1);

        when(categoryRepo.findByName(newCategory.getName())).thenReturn(null);

        Response result = productService.addCategory(newCategory);
        assertEquals("Category added successfully with ID: " + newCategory.getId(), result.getMessage());
    }

    @Test
    void testAddCategory_ExistingCategory() {
        Category existingCategory = new Category();
        existingCategory.setName("ExistingCategory");

        when(categoryRepo.findByName(existingCategory.getName())).thenReturn(existingCategory);

        Exception exception = assertThrows(KeyAlreadyExistsException.class, () -> {
            productService.addCategory(existingCategory);
        });
        assertEquals("Category name already exists.", exception.getMessage());
    }

    

    //Test for get category

    //existing category - by id
    @Test
    void testGetCategory_ExistingCategory_ById() {
        Integer categoryId = 1;
        Category existingCategory = new Category();
        existingCategory.setId(categoryId);
        existingCategory.setName("ExistingCategory");

        when(categoryRepo.findAllUsingId(categoryId)).thenReturn(List.of(existingCategory));
        when(categoryCache.containsKey(categoryId)).thenReturn(true);
        when(categoryCache.get(categoryId)).thenReturn(existingCategory);

        List<Category> result = productService.getCategory(categoryId);
        assertEquals(1, result.size());
        assertEquals(existingCategory.getName(), result.get(0).getName());
    }

    @Test
    void testGetCategory_AllCategories() {
        List<Category> categories = Arrays.asList(
            new Category(1, "Category1"),
            new Category(2, "Category2")
        );

        when(categoryRepo.findAll()).thenReturn(categories);

        List<Category> result = productService.getCategory(null);
        assertEquals(2, result.size());
        assertEquals(categories, result);
    }

    @Test
    void testGetCategory_NonExistingCategory_ById() {
        Integer categoryId = 1;

        when(categoryRepo.findAllUsingId(categoryId)).thenReturn(Collections.emptyList());

        Exception exception = assertThrows(NoSuchElementException.class, () -> {
            productService.getCategory(categoryId);
        });
        assertEquals("No category with that id found", exception.getMessage());
    }

    @Test
    void testGetCategory_NoCategoriesExist() {
        when(categoryRepo.findAll()).thenReturn(Collections.emptyList());

        Exception exception = assertThrows(NoSuchElementException.class, () -> {
            productService.getCategory(null);
        });
        assertEquals("No categories exist.", exception.getMessage());
    }

    //update category

    @Test
    void testUpdateCategory_ExistingCategory() {
        Integer categoryId = 1;
        String newName = "NewCategoryName";

        Category existingCategory = new Category();
        existingCategory.setId(categoryId);
        existingCategory.setName("ExistingCategoryName");

        when(categoryRepo.findUsingId(categoryId)).thenReturn(existingCategory);
        when(categoryRepo.findByName(newName)).thenReturn(null);

        Response result = productService.updateCategory(categoryId, newName);
        assertEquals("successfully updated category's name", result.getMessage());
    }

    @Test
    void testUpdateCategory_CategoryDoesNotExist() {
        Integer categoryId = 1;
        String newName = "NewCategoryName";

        when(categoryRepo.findUsingId(categoryId)).thenReturn(null);

        Exception exception = assertThrows(NoSuchElementException.class, () -> {
            productService.updateCategory(categoryId, newName);
        });
        assertEquals("Cateogry with given id does not exist", exception.getMessage());
    }

    @Test
    void testUpdateCategory_CategoryNameAlreadyExists() {
        Integer categoryId = 1;
        String existingName = "ExistingCategoryName";

        Category existingCategory = new Category();
        existingCategory.setId(categoryId);
        existingCategory.setName("ExistingCategoryName");

        when(categoryRepo.findUsingId(categoryId)).thenReturn(existingCategory);
        when(categoryRepo.findByName(existingName)).thenReturn(existingCategory);

        Exception exception = assertThrows(KeyAlreadyExistsException.class, () -> {
            productService.updateCategory(categoryId, existingName);
        });
        assertEquals("Category name already exists.", exception.getMessage());
    }

    //delete category

    @Test
    void testDeleteCategory_ExistingCategory() {
        Integer categoryId = 1;

        Category existingCategory = new Category();
        existingCategory.setId(categoryId);
        existingCategory.setName("ExistingCategory");

        when(categoryRepo.findUsingId(categoryId)).thenReturn(existingCategory);
        when(productRepo.findByCategoryId(categoryId)).thenReturn(Collections.emptyList());

        Response result = productService.deleteCategory(categoryId);
        assertEquals("Successfully deleted cateogry with id " + categoryId, result.getMessage());
    }

    @Test
    void testDeleteCategory_CategoryDoesNotExist() {
        Integer categoryId = 1;

        when(categoryRepo.findUsingId(categoryId)).thenReturn(null);

        Exception exception = assertThrows(NoSuchElementException.class, () -> {
            productService.deleteCategory(categoryId);
        });
        assertEquals("category with id " + categoryId + " doesnt exist!!", exception.getMessage());
    }

    @Test
    void testDeleteCategory_CategoryHasProducts() {
        Integer categoryId = 1;

        Category existingCategory = new Category();
        existingCategory.setId(categoryId);
        existingCategory.setName("ExistingCategory");

        Product product = new Product();
        product.setId(1);
        product.setCategory(existingCategory);

        when(categoryRepo.findUsingId(categoryId)).thenReturn(existingCategory);
        when(productRepo.findByCategoryId(categoryId)).thenReturn(List.of(product));

        Exception exception = assertThrows(IllegalStateException.class, () -> {
            productService.deleteCategory(categoryId);
        });
        assertEquals("Category cannot be deleted.There are products in that category", exception.getMessage());
    }

    //sell product

    @Test
    void testSellProduct_ExistingProduct() {
        Integer productId = 1;
        Integer quantity = 2;

        Product existingProduct = new Product();
        existingProduct.setId(productId);
        existingProduct.setQuantity(10);

        when(productRepo.findUsingId(productId)).thenReturn(existingProduct);

        Response result = productService.sellProduct(productId, quantity);
        assertEquals(quantity + " products sold. Remaining quantity = " + (existingProduct.getQuantity()), result.getMessage());
    }

    @Test
    void testSellProduct_ProductDoesNotExist() {
        Integer productId = 1;
        Integer quantity = 2;

        when(productRepo.findUsingId(productId)).thenReturn(null);

        Exception exception = assertThrows(NoSuchElementException.class, () -> {
            productService.sellProduct(productId, quantity);
        });
        assertEquals("Product with given id does not exist", exception.getMessage());
    }

    @Test
    void testSellProduct_InvalidQuantity() {
        Integer productId = 1;
        Integer quantity = -1;

        Product existingProduct = new Product();
        existingProduct.setId(productId);
        existingProduct.setQuantity(10);

        when(productRepo.findUsingId(productId)).thenReturn(existingProduct);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            productService.sellProduct(productId, quantity);
        });
        assertEquals("Enter valid quantity", exception.getMessage());
    }

    @Test
    void testSellProduct_InsufficientQuantity() {
        Integer productId = 1;
        Integer quantity = 11;

        Product existingProduct = new Product();
        existingProduct.setId(productId);
        existingProduct.setQuantity(10);

        when(productRepo.findUsingId(productId)).thenReturn(existingProduct);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            productService.sellProduct(productId, quantity);
        });
        assertEquals("Required quantity not available.Available quantity = " + existingProduct.getQuantity(),exception.getMessage());
    }

     //Restock products - success
    @Test
    void testRestockProduct_Success() {
        Integer productId = 1;
        Integer quantity = 200;

        Product existingProduct = new Product();
        existingProduct.setId(1);
        existingProduct.setQuantity(100);
        existingProduct.setName("Mobile");

        when(productRepo.findUsingId(1)).thenReturn(existingProduct);

        Response response = productService.restockProduct(productId, quantity);

        assertEquals("200 products restocked. Updated quantity = 300", response.getMessage());
        verify(productRepo, times(1)).save(existingProduct);
        verify(productCache, times(1)).put(productId, existingProduct);
    }

    //Restock - product not found
    @Test
    void testRestockProduct_ProductDoesNotExist() {
        Integer productId = 1;

        when(productRepo.findUsingId(productId)).thenReturn(null);

        Exception exception = assertThrows(NoSuchElementException.class, () -> {
            productService.restockProduct(productId,100);
        });
        assertEquals("Product with given id does not exist", exception.getMessage());
    }

     //Restock products - invalid quantity
     @Test
     void testRestockProduct_InvalidQuantity() {
         Integer productId = 1;
         Integer quantity = -200;
 
         Product existingProduct = new Product();
         existingProduct.setId(1);
         existingProduct.setQuantity(100);
         existingProduct.setName("Mobile");
 
         when(productRepo.findUsingId(1)).thenReturn(existingProduct);
 
         Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            productService.restockProduct(productId,quantity);
        });
        assertEquals("Enter valid quantity", exception.getMessage());
     }
}
