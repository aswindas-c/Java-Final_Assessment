package com.Aspire.Respository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.Aspire.model.Manager;


public interface ManagerRepository extends JpaRepository<Manager,Integer>{

    @Query("SELECT e FROM Manager e WHERE e.id = :id")
    Manager findUsingid(Integer id);
}
