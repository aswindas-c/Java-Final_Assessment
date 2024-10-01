package com.example.Inventory_Management;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
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
}
