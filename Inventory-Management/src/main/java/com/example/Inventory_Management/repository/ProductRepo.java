package com.example.Inventory_Management.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import com.example.Inventory_Management.model.Product;

@Repository
public interface ProductRepo extends JpaRepository<Product,Integer> {

    @Query("SELECT MAX(e.id) FROM Product e")
    Integer findMaxId();

    Object findByName(String name);

    boolean existsById(Integer id);

    List<Product> findByCategoryId(Integer categoryId);

    @Query("SELECT e FROM Product e WHERE e.id = :productId and e.categoryId = :categoryId")
    List<Product> findByCategoryIdandId(Integer productId,Integer categoryId);

    @Query("SELECT e FROM Product e WHERE e.id = :id")
    List<Product> findUsingId(Integer id);
}
