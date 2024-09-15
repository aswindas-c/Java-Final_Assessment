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
}
