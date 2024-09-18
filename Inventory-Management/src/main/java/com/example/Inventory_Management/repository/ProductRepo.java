package com.example.Inventory_Management.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import com.example.Inventory_Management.model.Product;

@Repository
public interface ProductRepo extends JpaRepository<Product,Integer> {

    @Query("SELECT MAX(e.id) FROM Product e")
    Integer findMaxId();

    
}
