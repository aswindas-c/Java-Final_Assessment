package com.Aspire.Respository;


import java.util.List;

import com.Aspire.model.Employee;


public interface EmployeeRepository {
    List<Employee> findByStream(String stream);
    Integer findMaxId();
    Employee save(Employee employee);
    Employee insert(Employee Employee);
    boolean existsById(Integer id);
    Employee findById(Integer id);
    List<Employee> findByManagerId(Integer managerId);
    void delete(Employee employee);
    List<Employee> findAll();
    List<Employee> findByNameStartsWith(String startsWith);
}
