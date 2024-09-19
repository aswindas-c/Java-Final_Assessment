package com.Aspire.Respository;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.Aspire.model.Employee;


public interface EmployeeRepository extends JpaRepository<Employee,Integer>{
    List<Employee> findByStream(String stream);

    @Query("SELECT COALESCE(MAX(e.id), 0) FROM Employee e")
    Integer findMaxId();
    boolean existsById(Integer id);
    
    @Query("SELECT e FROM Employee e WHERE e.id = :id")
    Employee findUsingId(Integer id);
    List<Employee> findByManagerId(Integer managerId);
    List<Employee> findAll();

    @Query("SELECT e FROM Employee e WHERE LOWER(e.name) LIKE LOWER(CONCAT(:startsWith, '%'))")
    List<Employee> findByNameStartsWith(String startsWith);
}
