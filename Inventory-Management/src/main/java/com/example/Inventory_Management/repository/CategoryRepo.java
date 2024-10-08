package com.example.Inventory_Management.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import com.example.Inventory_Management.model.Category;



@Repository
public interface CategoryRepo extends JpaRepository<Category,Integer> {

    Object findByName(String name);

    boolean existsById(Integer id);
    
    @Query("SELECT e FROM Category e WHERE e.id = :id")
    Category findUsingId(Integer id);

    @Query("SELECT e FROM Category e WHERE e.id = :id")
    List<Category> findAllUsingId(Integer id);
}
