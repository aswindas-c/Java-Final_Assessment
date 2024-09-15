package com.Aspire.Respository;


import java.time.Instant;
import java.util.List;

import com.Aspire.model.Employee;


public interface EmployeeRepository {
    List<Employee> findByManagerIdAndDateOfJoiningBefore(Integer managerId, Instant minJoiningDate);
    List<Employee> findByManagerId(Integer managerId);
    List<Employee> findByDateOfJoiningBefore(Instant minJoiningDate);
    List<Employee> findByStream(String stream);
    Integer findMaxId();
    Employee save(Employee Employee);
    Employee insert(Employee Employee);
    void delete(Employee Employee);
    boolean existsById(Integer id);
    Employee findById(Integer id);
    List<Employee> findAll();
}
