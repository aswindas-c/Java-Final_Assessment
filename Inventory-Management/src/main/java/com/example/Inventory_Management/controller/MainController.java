package com.example.Inventory_Management.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.Inventory_Management.DTO.Response;
import com.example.Inventory_Management.model.Product;
import com.example.Inventory_Management.service.ProductService;

@RestController
@RequestMapping("/api")
public class MainController {
    @Autowired
    private ProductService productService;

    @PostMapping("/addProduct")
    public Response addEmployee(@RequestBody Product product) {
        return ProductService.addProduct(product);
    }

    // @GetMapping("/getEmployee")
    // public List<ManagerResponse> getEmployee(
    //         @RequestParam(required = false) Integer managerId,
    //         @RequestParam(required = false) Integer yearsOfExperience) {
    //     return employeeService.getEmployee(managerId, yearsOfExperience);
    // }

    // @DeleteMapping("/deleteEmployee")
    // public Response deleteEmployee(@RequestParam Integer employeeId) {
    //     return employeeService.deleteEmployee(employeeId);
    // }

    // @PutMapping("/changeEmployeeManager")
    // public Response changeEmployeeManager(
    //         @RequestBody ChangeManagerRequest request) {
    //     return employeeService.changeEmployeeManager(request.getEmployeeId(), request.getManagerId());
    // }
}
