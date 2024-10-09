package com.Aspire.service;
 
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import javax.management.openmbean.KeyAlreadyExistsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.Aspire.DTO.EmployeeResponseDto;
import com.Aspire.DTO.Response;
import com.Aspire.Respository.AccountRepository;
import com.Aspire.Respository.EmployeeRepository;
import com.Aspire.Respository.StreamRepository;
import com.Aspire.model.Account;
import com.Aspire.model.Employee;
import com.Aspire.model.Stream;
 
@Service
public class EmployeeService {
 
    @Autowired
    private EmployeeRepository employeeRepo;
 
    @Autowired
    private StreamRepository streamRepo;
 
    @Autowired
    private AccountRepository accountRepo;
 
    public Response addEmployee(Employee employee) {

        if(employee.getName() == null || employee.getDesignation() == null || employee.getAccountName() == null || employee.getManagerId() == null || employee.getStream() == null)
        {
            throw new IllegalArgumentException("Name,designation,stream,account,managerId Required");
        }
 
        String stream = employee.getStream();
        String designation = employee.getDesignation();
        String accountName = employee.getAccountName();
 
        // Validate employee data
        validateEmployeeData(stream, designation, accountName);
 
        // Handle special case for Account Manager(check if managerId = 0)
        if ("Manager".equalsIgnoreCase(employee.getDesignation())) {
            if (employee.getManagerId() != 0) {
                throw new IllegalArgumentException("Manager must have Manager ID set to 0. Employee cannot be added.");
            }
 
            //Update Manager id in the stream table and also check manager already exist for that stream
            Stream str = streamRepo.findByName(stream);
            if(str.getManagerId() != 0){
                throw new KeyAlreadyExistsException("A manager already exists in the stream: " + employee.getStream());
                
            }
            else{

                //save to employee collection
                employeeRepo.save(employee);
                str.setManagerId(employee.getId());
                streamRepo.save(str);
            }
 
            return new Response("Employee added as Manager successfully with ID: " + employee.getId());
        } else {
            // Handle non-Account Manager
            if (employee.getManagerId() == 0) {
                throw new IllegalArgumentException("Employee with Manager ID 0 should have designation as Manager. Employee cannot be added.");
            }
        }
 
        // Handle normal employee
        
        Employee manager = employeeRepo.findUsingId(employee.getManagerId());

        //check whether a manger with given managerId exist
        if (manager == null) {
            throw new NoSuchElementException("Manager with ID " + employee.getManagerId() + " not found. Employee cannot be added.");
        }
        
        //check if the employee with the given managerId is actually a manager
        if (manager.getManagerId() != 0) {
            throw new NoSuchElementException("Employee with ID " + employee.getManagerId() + " is not a manager. Employee cannot be added.");
        }
 
        // Check if employee and manager are in the same stream
        if (!employee.getStream().equalsIgnoreCase(manager.getStream())) {
            throw new IllegalArgumentException("Employee and manager must belong to the same stream. Employee cannot be added.");
        }
 
        // Save the employee
        employeeRepo.save(employee);
 
        return new Response("Employee added successfully under Manager with ID: " + manager.getId());
    }
 
    public void validateEmployeeData(String stream, String designation, String accountName) {
        List<String> errors = new ArrayList<>();
       
        if (!"Manager".equalsIgnoreCase(designation) && !"associate".equalsIgnoreCase(designation)) {
            errors.add("Designation can only be Manager or associate.");
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
        if(acnt!=null && str!=null){
            if (!str.getAccountId().equalsIgnoreCase(acnt.getId())) {
                errors.add("Stream does not belong to this account!!");
            }
        }
        if (!errors.isEmpty()) {
            throw new IllegalArgumentException(String.join(", ", errors));
        }
    }
    // Get employee starting with specific character
    public List<EmployeeResponseDto> getEmployee(String startsWith) {
        List<Employee> employees;
        if (startsWith == null || startsWith.isEmpty()) {
            if(employeeRepo.findAll().isEmpty())
            {
                throw new NoSuchElementException("No Employee found.");
            }
            else
            {
                employees = employeeRepo.findAll();
            }
        } else
            {
                if(employeeRepo.findByNameStartsWith(startsWith).isEmpty())
                {
                    throw new NoSuchElementException("No Employee found.");
                }
                else
                {
                    employees = employeeRepo.findByNameStartsWith(startsWith);
                }
            }
        return employees.stream().map(employee -> new EmployeeResponseDto(employee)).collect(Collectors.toList());
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
        if(employeeId == null)
        {
            throw new IllegalArgumentException("Enter valid Employee Id");
        }
        // Check if the employee exists
        Employee employee = employeeRepo.findUsingId(employeeId);
        if (employee == null) {
            throw new NoSuchElementException("Employee with ID " + employeeId + " not found.");
        }
        
        //check if the employee is a manager and has subordinates
        List<Employee> subordinates = employeeRepo.findByManagerId(employeeId);
        if (!subordinates.isEmpty()) {
            throw new IllegalStateException("Cannot delete Employee with ID " + employeeId + " as they are a manager with subordinates.");
        }
   
        // Updating the stream table by setting the manager id of the particular stream to 0
        if(employee.getManagerId() == 0)
        {
            Stream stream = streamRepo.findByName(employee.getStream());
            stream.setManagerId(0);
            streamRepo.save(stream);
        }
        employeeRepo.delete(employee);
        return new Response("Successfully deleted " + employee.getName() + " from the organization.");
    }
 
    //Change Employee's Manager
    public Response changeManager(Integer employeeId, Integer newManagerId) {
        if(employeeId == null || newManagerId == null)
        {
            throw new IllegalArgumentException("Enter valid Employee Id");
        }
        // Fetch the employee
        Employee employee = employeeRepo.findUsingId(employeeId);
        if (employee == null) {
            throw new NoSuchElementException("Employee with ID " + employeeId + " not found.");
        }
   
        if (employee.getManagerId() == 0) {
            throw new IllegalStateException("Employee is a manager, so cannot be changed");
        }
 
        if (employee.getManagerId().equals(newManagerId)) {
            throw new IllegalStateException("Employee is currently under the given manager. No changes required.");
        }
   
        // Fetch the new manager
        Employee newManager = employeeRepo.findUsingId(newManagerId);
        if (newManager == null || newManager.getManagerId()!=0) {
            throw new NoSuchElementException("New manager with ID " + newManagerId + " not found.");
        }
        
        //setting the stream and account of the employee to new manager's stream and account
        if (!employee.getStream().equalsIgnoreCase(newManager.getStream())) {
            employee.setStream(newManager.getStream());
            employee.setAccountName(newManager.getAccountName());
        }
   
        // Build the response
        String originalManagerName = employeeRepo.findUsingId(employee.getManagerId()).getName();
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
        if(employeeId == null || streamname == null)
        {
            throw new IllegalArgumentException("Enter valid Employee Id");
        }

        // Fetch the employee
        Employee employee = employeeRepo.findUsingId(employeeId);
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
            throw new NoSuchElementException("Stream does not exist!!");
        }
        //Check whether an manager exists in that stream
        else if (str.getManagerId() != 0) {
            throw new KeyAlreadyExistsException("A manager already exists in the stream: " + employee.getStream());
        }
        else{
            if(!str.getAccountId().equalsIgnoreCase(acnt.getId()))
            {
                employee.setAccountName(accountRepo.findUsingId(str.getAccountId()).getName());
            }
            employee.setStream(streamname);
            employee.setDesignation("Manager");
 
            //save to employee collection
            employeeRepo.save(employee);
            
            //set manager id in stream
            str.setManagerId(employee.getId());
                streamRepo.save(str);
            return new Response(
                employee.getName() + " has been promoted to Manager of "+employee.getStream());
        }
    }
 
    //Change Employee Account
    public Response changeAccount(Integer employeeId, String account,String streamName) {

        if(employeeId == null || account == null || streamName == null)
        {
            throw new IllegalArgumentException("Enter valid Employee Id");
        }
        // Fetch the employee
        Employee employee = employeeRepo.findUsingId(employeeId);
        Stream str = streamRepo.findByName(streamName);
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
        //Check whether employee is in that account
        else if (employee.getAccountName().equalsIgnoreCase(account)) {
            throw new IllegalStateException("Employee is already in the "+ account + " account");
        }
        //Check whether a manager exists in that stream
        else if (str.getManagerId() == 0) {
            throw new KeyAlreadyExistsException("No manager found for stream : " + str.getName());
        }
        //check whether the employee is a manager
        else if(employee.getManagerId() == 0){
            throw new IllegalStateException("Cannot change account, Employee is a manager");
        }
        else{
            employee.setAccountName(acnt.getName());
            employee.setStream(streamName);
            employee.setAccountName(account);
            employee.setManagerId(str.getManagerId());
 
            //save to employee collection
            employeeRepo.save(employee);
            return new Response(
                employee.getName() + " account has been changed");
        }
    }
}