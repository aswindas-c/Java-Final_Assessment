package com.example.Inventory_Management.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import com.example.Inventory_Management.model.Category;



@Repository
public interface CategoryRepo extends JpaRepository<Category,Integer> {

    Object findByName(String name);

    @Query("SELECT MAX(e.id) FROM Category e")
    Integer findMaxId();

    boolean existsById(Integer id);
}
