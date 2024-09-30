package com.Aspire;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;

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
        employee.setAccountName("SmartOps");
        employee.setDesignation("Associate");
        employee.setStream("SmartOps-Sales");
        employee.setManagerId(2);

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

    //Add Manager Successfully
    @Test
    void testAddEmployee_Manager_Success() {
        
        Employee employee = new Employee();
        employee.setName("Aswin");
        employee.setAccountName("SmartOps");
        employee.setDesignation("Manager");
        employee.setStream("SmartOps-Sales");
        employee.setManagerId(0);

        Stream stream = new Stream();
        stream.setName("SmartOps-Sales");
        stream.setAccountId("SO");
        stream.setManagerId(0);

        Account account = new Account();
        account.setName("SmartOps");
        account.setId("SO");

        when(employeeRepo.existsById(2)).thenReturn(false);
        when(streamRepo.findByName(employee.getStream())).thenReturn(stream);
        when(accountRepo.findByName(employee.getAccountName())).thenReturn(account);
        when(employeeRepo.findMaxId()).thenReturn(null);
       
        Response response = employeeService.addEmployee(employee);

        assertEquals("Employee added as Manager successfully with ID: 1", response.getMessage());  // ID would be null unless mock behavior is adjusted.
        verify(employeeRepo, times(1)).save(any(Employee.class));
    }

     //Manager Id 0 should have designation Manager
     @Test
     void testAddEmployee_Manager_Designation() {
         
         Employee employee = new Employee();
         employee.setName("Aswin");
         employee.setAccountName("SmartOps");
         employee.setDesignation("Associate");
         employee.setStream("SmartOps-Sales");
         employee.setManagerId(0);
 
         Stream stream = new Stream();
         stream.setName("SmartOps-Sales");
         stream.setAccountId("SO");
 
         Account account = new Account();
         account.setName("SmartOps");
         account.setId("SO");
 
         when(employeeRepo.existsById(2)).thenReturn(false);
         when(streamRepo.findByName(employee.getStream())).thenReturn(stream);
         when(accountRepo.findByName(employee.getAccountName())).thenReturn(account);
         when(employeeRepo.findMaxId()).thenReturn(null);
        
         Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            employeeService.addEmployee(employee);
        });

        assertEquals("Manager ID 0 should have designation as Manager. Employee cannot be added.", exception.getMessage());
    }

    //Manager Not Found
    @Test
    void testAddEmployee_Manager_Not_Found() {
        
        Employee employee = new Employee();
        employee.setName("Aswin");
        employee.setAccountName("SmartOps");
        employee.setDesignation("Associate");
        employee.setStream("SmartOps-Sales");
        employee.setManagerId(2);

        Stream stream = new Stream();
        stream.setName("SmartOps-Sales");
        stream.setAccountId("SO");

        Account account = new Account();
        account.setName("SmartOps");
        account.setId("SO");

        when(employeeRepo.existsById(1)).thenReturn(false);
        when(employeeRepo.findUsingId(employee.getManagerId())).thenReturn(null);
        when(streamRepo.findByName(employee.getStream())).thenReturn(stream);
        when(accountRepo.findByName(employee.getAccountName())).thenReturn(account);
       
        Exception exception = assertThrows(NoSuchElementException.class, () -> {
            employeeService.addEmployee(employee);
        });

        assertEquals("Manager with ID 2 not found. Employee cannot be added.", exception.getMessage());
    }

    //Given employee with managerid is not a manager
    @Test
    void testAddEmployee_Not_Manager() {
        
        Employee employee = new Employee();
        employee.setName("Aswin");
        employee.setAccountName("SmartOps");
        employee.setDesignation("Associate");
        employee.setStream("SmartOps-Sales");
        employee.setManagerId(2);

        Employee manager = new Employee();
        manager.setId(2);
        manager.setManagerId(4);

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
       
        Exception exception = assertThrows(NoSuchElementException.class, () -> {
            employeeService.addEmployee(employee);
        });

        assertEquals("Employee with ID 2 is not a manager. Employee cannot be added.", exception.getMessage());
    }

    //Employee and Manager Different Stream
    @Test
    void testAddEmployee_Stream_Mismatch() {
        
        Employee employee = new Employee();
        employee.setName("Aswin");
        employee.setAccountName("SmartOps");
        employee.setDesignation("Associate");
        employee.setStream("SmartOps-Sales");
        employee.setManagerId(2);

        Employee manager = new Employee();
        manager.setId(2);
        manager.setManagerId(0);
        manager.setStream("Walmart-Sales");

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
       
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            employeeService.addEmployee(employee);
        });

        assertEquals("Employee and manager must belong to the same stream. Employee cannot be added.", exception.getMessage());
    }

    //Employee Starts with A
    @Test
    void testGetEmployee_GetEmployeesStartingWith() {
        Employee employee1 = new Employee();
        employee1.setName("Aswin");

        List<Employee> employees = Arrays.asList(employee1);
        when(employeeRepo.findByNameStartsWith("A")).thenReturn(employees);

        List<Employee> result = employeeService.getEmployee("A");

        assertEquals(employees, result);
    }

    //No Employee Starts with A
    @Test
    void testGetEmployee_No_EployeesStartingWith() {

        List<Employee> employees = Arrays.asList();
        when(employeeRepo.findByNameStartsWith("A")).thenReturn(employees);

        Exception exception = assertThrows(NoSuchElementException.class, () -> {
            employeeService.getEmployee("A");
        });

        assertEquals("No Employee found.", exception.getMessage());
    }

    //Get all Employee
    @Test
    void testGetEmployee_All_Employee() {
        Employee employee1 = new Employee();
        employee1.setName("Aswin");

        Employee employee2 = new Employee();
        employee2.setName("Riya");

        List<Employee> employees = Arrays.asList(employee1,employee2);
        when(employeeRepo.findAll()).thenReturn(employees);
        List<Employee> result = employeeService.getEmployee(null);

        assertEquals(employees, result);
    }

    //No Employee present
    @Test
    void testGetEmployee_No_Employee_Present() {

        List<Employee> employees = Arrays.asList();
        when(employeeRepo.findAll()).thenReturn(employees);
        Exception exception = assertThrows(NoSuchElementException.class, () -> {
            employeeService.getEmployee(null);
        });

        assertEquals("No Employee found.", exception.getMessage());
    }

    //No Streams present
    @Test
    void testGetStreams_NO_Stream() {

        List<Stream> streams = Arrays.asList();
        when(streamRepo.findAll()).thenReturn(streams);
        Exception exception = assertThrows(NoSuchElementException.class, () -> {
            employeeService.getStreams();
        });

        assertEquals("No Streams found.", exception.getMessage());
    }

    //Get all Stream
    @Test
    void testGetStream_All_Streams() {
        Stream stream1 = new Stream();
        stream1.setName("SmartOps-Sales");

        Stream stream2 = new Stream();
        stream2.setName("Walmart-Delivery");

        List<String> streamNames = Arrays.asList(stream1.getName(),stream2.getName());
        List<Stream> streams = Arrays.asList(stream1,stream2);
        when(streamRepo.findAll()).thenReturn(streams);
        List<String> result = employeeService.getStreams();

        assertEquals(streamNames, result);
    }
}
