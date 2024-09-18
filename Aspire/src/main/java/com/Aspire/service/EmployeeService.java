package com.Aspire.service;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import javax.management.openmbean.KeyAlreadyExistsException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.Aspire.DTO.Response;
import com.Aspire.Respository.AccountRepository;
import com.Aspire.Respository.EmployeeRepository;
import com.Aspire.Respository.ManagerRepository;
import com.Aspire.Respository.StreamRepository;
import com.Aspire.model.Account;
import com.Aspire.model.Employee;
import com.Aspire.model.Manager;
import com.Aspire.model.Stream;

@Service
public class EmployeeService {

    @Autowired
    private EmployeeRepository employeeRepo;

    @Autowired
    private ManagerRepository managerRepo;

    @Autowired
    private StreamRepository streamRepo;

    @Autowired
    private AccountRepository accountRepo;

    public Response addEmployee(Employee employee) {

        String stream = employee.getStream();
        String designation = employee.getDesignation();
        String accountName = employee.getAccountName();
 
        // Validate employee data
        validateEmployeeData(stream, designation, accountName);

        Integer maxId = employeeRepo.findMaxId();
        if(maxId != null) {
            employee.setId(maxId + 1);
        } else {
            employee.setId(1);
        }
        
        // Check if employee with the same ID already exists
        if (employeeRepo.existsById(employee.getId())) {
            throw new KeyAlreadyExistsException("Employee ID already exists.");
        }

        // Handle special case for Account Manager
        if ("Account Manager".equalsIgnoreCase(employee.getDesignation())) {
            if (employee.getManagerId() != 0) {
                throw new IllegalArgumentException("Account Manager must have Manager ID set to 0. Employee cannot be added.");
            }

            //Update Manager id to stream collection and also check manager already exist for the stream
            Stream str = streamRepo.findByName(stream);
            if(str.getManagerId() == 0){
                str.setManagerId(employee.getId());
                streamRepo.save(str);
            }
            else{
                throw new KeyAlreadyExistsException("A manager already exists in the stream: " + employee.getStream());
            }


            //save to employee collection
            employeeRepo.insert(employee);

            // Save the manager details to the Manager collection
            Manager manager = new Manager();
            manager.setId(employee.getId());
            manager.setName(employee.getName());
            manager.setStreamName(employee.getStream());
            // Add the manager to the Manager collection
            managerRepo.insert(manager);

            return new Response("Employee added as Manager successfully with ID: " + employee.getId());
        } else {
            // Handle non-Account Manager 
            if (employee.getManagerId() == 0) {
                throw new IllegalArgumentException("Manager ID 0 should have designation as Account Manager. Employee cannot be added.");
            }
        }

        // Handle normal employee
        Employee manager = employeeRepo.findById(employee.getManagerId());
        if (manager == null) {
            throw new NoSuchElementException("Manager with ID " + employee.getManagerId() + " not found. Employee cannot be added.");
        }

        if (manager.getManagerId() != 0) {
            throw new NoSuchElementException("Employee with ID " + employee.getManagerId() + " is not a manager. Employee cannot be added.");
        }

        // Check if employee and manager are in the same stream
        if (!employee.getStream().equalsIgnoreCase(manager.getStream())) {
            throw new IllegalArgumentException("Employee and manager must belong to the same stream. Employee cannot be added.");
        }

        // Check if employee and manager are in the same Account
        if (!employee.getAccountName().equalsIgnoreCase(manager.getAccountName())) {
            throw new IllegalArgumentException("Employee and manager must belong to the same account. Employee cannot be added.");
        }

        // Save the employee
        employeeRepo.insert(employee);

        return new Response("Employee added successfully under Manager with ID: " + manager.getId());
    }

    public void validateEmployeeData(String stream, String designation, String accountName) {
        List<String> errors = new ArrayList<>();
        
        if (!"Account Manager".equalsIgnoreCase(designation) && !"associate".equalsIgnoreCase(designation)) {
            errors.add("Designation can only be Account Manager or associate.");
        }
     
        //Check Stream exist in DB
        Stream str = streamRepo.findByName(stream);
        if (str == null) {
            errors.add("Stream not found!!");
        }

        //Check account exist in DB
        Account acnt = accountRepo.findByName(accountName);
        if (acnt == null) {
            errors.add("Account not found!!");
        }
    
        //Check Stream belong to that account
        if (!str.getAccountId().equalsIgnoreCase(acnt.getId())) {
            errors.add("Stream does not belong to this account!!");
        }
        if (!errors.isEmpty()) {
            throw new IllegalArgumentException(String.join(", ", errors));
        }
    }
    // Get employee starting with specific character
    public List<Employee> getEmployee(String startsWith) {
        if (startsWith == null || startsWith.isEmpty()) {
            if(employeeRepo.findAll().isEmpty())
            {
                throw new NoSuchElementException("No Employee found.");
            }
            else
            {
                return employeeRepo.findAll();
            }
        } else 
        {
            if(employeeRepo.findByNameStartsWith(startsWith).isEmpty())
            {
                throw new NoSuchElementException("No Employee found.");
            }
            else
            {
                return employeeRepo.findByNameStartsWith(startsWith);
            }
        }
    }

    //Get all streams
    public List<String> getStreams() {
        List<Stream> streams = streamRepo.findAll();
        if(streams.isEmpty())
        {
            throw new NoSuchElementException("No Streams found.");
        }
        else{
            List<String> streamNames = new ArrayList<>();
            for (Stream stream : streams) {
                streamNames.add(stream.getName());
            }
            return streamNames;
        }
    }

    //Delete a employee
    public Response deleteEmployee(Integer employeeId) {
        // Check if the employee exists
        Employee employee = employeeRepo.findById(employeeId);
        if (employee == null) {
            throw new NoSuchElementException("Employee with ID " + employeeId + " not found.");
        }
    
        List<Employee> subordinates = employeeRepo.findByManagerId(employeeId);
        if (!subordinates.isEmpty()) {
            throw new IllegalStateException("Cannot delete Employee with ID " + employeeId + " as they are a manager with subordinates.");
        }
    
        // Delete the employee\
        if(employee.getManagerId() == 0)
        {
            Manager manager = managerRepo.findById(employee.getId());
            Stream stream = streamRepo.findByName(employee.getStream());
            managerRepo.delete(manager);
            stream.setManagerId(0);
            streamRepo.save(stream);
        }
        employeeRepo.delete(employee);
        return new Response("Successfully deleted " + employee.getName() + " from the organization.");
    }

    //Change Employee Manager
    public Response changeManager(Integer employeeId, Integer newManagerId) {
        // Fetch the employee
        Employee employee = employeeRepo.findById(employeeId);
        if (employee == null) {
            throw new NoSuchElementException("Employee with ID " + employeeId + " not found.");
        }
    
        if (employee.getManagerId() == 0) {
            throw new IllegalStateException("Employee is a manager cannot be changed");
        }

        if (employee.getManagerId().equals(newManagerId)) {
            throw new IllegalStateException("Employee is currently under the given manager. No changes required.");
        }
    
        // Fetch the new manager
        Employee newManager = employeeRepo.findById(newManagerId);
        if (newManager == null || newManager.getManagerId()!=0) {
            throw new NoSuchElementException("New manager with ID " + newManagerId + " not found.");
        }
    
        if (!employee.getStream().equalsIgnoreCase(newManager.getStream())) {
            employee.setStream(newManager.getStream());
            employee.setAccountName(newManager.getAccountName());
        }
    
        // Build the response
        String originalManagerName = employeeRepo.findById(employee.getManagerId()).getName();
        String newManagerName = newManager.getName();

        // Update the employee manager ID and updatedTime
        employee.setManagerId(newManagerId);
        employeeRepo.save(employee);
    
        return new Response(
                employee.getName() + "'s manager has been successfully changed from " + originalManagerName + " to " + newManagerName + "."
        );
    }

    //Change Employee Designation
    public Response changeDesignation(Integer employeeId, String streamname) {
        // Fetch the employee
        Employee employee = employeeRepo.findById(employeeId);
        //Check if employee with that id exist
        if (employee == null) {
            throw new NoSuchElementException("Employee with ID " + employeeId + " not found.");
        }
        Stream str = streamRepo.findByName(streamname);
        Account acnt = accountRepo.findByName(employee.getAccountName());
        //Check whether he is still a manager
        if (employee.getManagerId() == 0) {
            throw new IllegalStateException("Employee is already a manager");
        }
        //Check whether the stream exists
        else if (str == null) {
            throw new NoSuchElementException("Stream does not exist found!!");
        }
        //Check whether an manager exists in that stream
        else if (str.getManagerId() != 0) {
            throw new KeyAlreadyExistsException("A manager already exists in the stream: " + employee.getStream());
        }
        else{
            if(!str.getAccountId().equalsIgnoreCase(acnt.getId()))
            {
                employee.setAccountName(acnt.getName());
            }
            employee.setStream(streamname);
            employee.setDesignation("Account Manager");

            //save to employee collection
            employeeRepo.save(employee);
            // Save the manager details to the Manager collection
            Manager manager = new Manager();
            manager.setId(employee.getId());
            manager.setName(employee.getName());
            manager.setStreamName(employee.getStream());
            // Add the manager to the Manager collection
            managerRepo.save(manager);
            str.setManagerId(employee.getId());
                streamRepo.save(str);
            return new Response(
                employee.getName() + " has been promoted to Manager");
        }
    }

    //Change Employee Account
    public Response changeAccount(Integer employeeId, String account,String streamname) {
        // Fetch the employee
        Employee employee = employeeRepo.findById(employeeId);
        Stream str = streamRepo.findByName(streamname);
        Account acnt = accountRepo.findByName(account);
        //Check if employee with that id exist
        if (employee == null) {
            throw new NoSuchElementException("Employee with ID " + employeeId + " not found.");
        }
        //Check whether the stream exists
        else if (str == null) {
            throw new NoSuchElementException("Stream does not exist!!");
        }
        //Check whether the account exists
        else if (acnt == null) {
            throw new NoSuchElementException("Account does not exist!!");
        }
        //Check Stream belong to that account
        else if (!str.getAccountId().equalsIgnoreCase(acnt.getId())) {
            throw new IllegalStateException("Stream does not belong to this account!!");
        }
        //Check whether he is in that account
        else if (employee.getAccountName() == account) {
            throw new IllegalStateException("Employee is already in the "+ account + " account");
        }
        //Check whether an manager exists in that stream
        else if (str.getManagerId() == 0) {
            throw new KeyAlreadyExistsException("No manager found for stream : " + employee.getStream());
        }
        else{
            if(!str.getAccountId().equalsIgnoreCase(acnt.getId()))
            {
                employee.setAccountName(acnt.getName());
            }
            employee.setStream(streamname);
            employee.setAccountName(account);
            employee.setManagerId(str.getManagerId());

            //save to employee collection
            employeeRepo.save(employee);
            return new Response(
                employee.getName() + " account has been changed");
        }
    }
}
