package com.Aspire;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


import javax.management.openmbean.KeyAlreadyExistsException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.Aspire.DTO.Response;
import com.Aspire.Respository.AccountRepository;
import com.Aspire.Respository.EmployeeRepository;
import com.Aspire.Respository.StreamRepository;
import com.Aspire.model.Account;
import com.Aspire.model.Employee;
import com.Aspire.model.Stream;
import com.Aspire.service.EmployeeService;

public class EmployeeServiceTest {
    @Mock
    private EmployeeRepository employeeRepo;

    @Mock
    private AccountRepository accountRepo;
    
    @Mock
    private StreamRepository streamRepo;

    @InjectMocks
    private EmployeeService employeeService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this); 
    }

    //Add Employee Successfully
    @Test
    void testAddEmployee_Success() {
        
        Employee employee = new Employee();
        employee.setName("Aswin");
        employee.setId(2);
        employee.setAccountName("SmartOps");
        employee.setDesignation("Associate");
        employee.setStream("SmartOps-Sales");
        employee.setManagerId(1);

        Employee manager = new Employee();
        manager.setId(2);
        manager.setManagerId(0);
        manager.setStream("SmartOps-Sales");

        Stream stream = new Stream();
        stream.setName("SmartOps-Sales");
        stream.setAccountId("SO");

        Account account = new Account();
        account.setName("SmartOps");
        account.setId("SO");

        when(employeeRepo.existsById(1)).thenReturn(false);
        when(employeeRepo.findUsingId(employee.getManagerId())).thenReturn(manager);
        when(streamRepo.findByName(employee.getStream())).thenReturn(stream);
        when(accountRepo.findByName(employee.getAccountName())).thenReturn(account);
       
        Response response = employeeService.addEmployee(employee);

        assertEquals("Employee added successfully under Manager with ID: 2", response.getMessage());  // ID would be null unless mock behavior is adjusted.
        verify(employeeRepo, times(1)).save(any(Employee.class));
    }

    //Validation Errors
    @Test
    void testAddEmployee_ValidationError() {
        
        Employee employee = new Employee();
        employee.setAccountName("SmartOps");
        employee.setDesignation("Asiate");
        employee.setStream("SmartOps-Sales");

       
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            employeeService.addEmployee(employee);
        });

        assertEquals("Designation can only be Manager or associate., Stream not found!!, Account not found!!", exception.getMessage());
    }

    //Stream - Account Mismatch
    @Test
    void testAddEmployee_ValidationErrorMismatch() {
        
        Employee employee = new Employee();
        employee.setAccountName("Walmart");
        employee.setDesignation("Associate");
        employee.setStream("SmartOps-Sales");

        Stream stream = new Stream();
        stream.setName("SmartOps-Sales");
        stream.setAccountId("SO");

        Account account = new Account();
        account.setName("Walmart");
        account.setId("WL");

        when(streamRepo.findByName(employee.getStream())).thenReturn(stream);
        when(accountRepo.findByName(employee.getAccountName())).thenReturn(account);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            employeeService.addEmployee(employee);
        });

        assertEquals("Stream does not belong to this account!!", exception.getMessage());
    }

    //Add Employee - Employee Id exists
    @Test
    void testAddEmployee_EmployeeIdExists() {
        
        Employee employee = new Employee();
        employee.setId(2);
        employee.setAccountName("SmartOps");
        employee.setDesignation("Associate");
        employee.setStream("SmartOps-Sales");

        Stream stream = new Stream();
        stream.setName("SmartOps-Sales");
        stream.setAccountId("SO");

        Account account = new Account();
        account.setName("SmartOps");
        account.setId("SO");

        when(employeeRepo.existsById(1)).thenReturn(true);
        when(streamRepo.findByName(employee.getStream())).thenReturn(stream);
        when(accountRepo.findByName(employee.getAccountName())).thenReturn(account);
       
        Exception exception = assertThrows(KeyAlreadyExistsException.class, () -> {
            employeeService.addEmployee(employee);
        });

        assertEquals("Employee ID already exists.", exception.getMessage());
    }

    //Add Employee - Manager must have managerid 0
    @Test
    void testAddEmployee_ManagerIdNotZero() {
        
        Employee employee = new Employee();
        employee.setId(2);
        employee.setAccountName("SmartOps");
        employee.setDesignation("Manager");
        employee.setStream("SmartOps-Sales");
        employee.setManagerId(3);

        Stream stream = new Stream();
        stream.setName("SmartOps-Sales");
        stream.setAccountId("SO");

        Account account = new Account();
        account.setName("SmartOps");
        account.setId("SO");

        when(employeeRepo.existsById(1)).thenReturn(false);
        when(streamRepo.findByName(employee.getStream())).thenReturn(stream);
        when(accountRepo.findByName(employee.getAccountName())).thenReturn(account);
       
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            employeeService.addEmployee(employee);
        });

        assertEquals("Manager must have Manager ID set to 0. Employee cannot be added.", exception.getMessage());
    }

    //Add Employee - Manager exists in stream
    @Test
    void testAddEmployee_ManagerExistInStream() {
        
        Employee employee = new Employee();
        employee.setId(2);
        employee.setAccountName("SmartOps");
        employee.setDesignation("Manager");
        employee.setStream("SmartOps-Sales");
        employee.setManagerId(0);

        Stream stream = new Stream();
        stream.setName("SmartOps-Sales");
        stream.setAccountId("SO");
        stream.setManagerId(4);

        Account account = new Account();
        account.setName("SmartOps");
        account.setId("SO");

        when(employeeRepo.existsById(1)).thenReturn(false);
        when(streamRepo.findByName(employee.getStream())).thenReturn(stream);
        when(accountRepo.findByName(employee.getAccountName())).thenReturn(account);
       
        Exception exception = assertThrows(KeyAlreadyExistsException.class, () -> {
            employeeService.addEmployee(employee);
        });

        assertEquals("A manager already exists in the stream: SmartOps-Sales", exception.getMessage());
    }
}
