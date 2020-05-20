package com.lambdaschool.sampleemps.controllers;

import com.lambdaschool.sampleemps.models.Employee;
import com.lambdaschool.sampleemps.models.ErrorDetail;
import com.lambdaschool.sampleemps.services.EmployeeService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/employees") // optional
public class EmployeeController
{
    @Autowired
    private EmployeeService employeeService;

    @ApiOperation(value = "returns all Employees",
        response = Employee.class,
        responseContainer = "List")
    @GetMapping(value = "/employees")
    public ResponseEntity<?> listAllEmployees()
    {
        List<Employee> myEmployees = employeeService.findAllEmployees();
        return new ResponseEntity<>(myEmployees,
            HttpStatus.OK);
    }

    @ApiOperation(value = "Retrieve an employee based off of employee id",
        response = Employee.class)
    @ApiResponses(value = {@ApiResponse(code = 200,
        message = "Employee Found",
        response = Employee.class), @ApiResponse(code = 404,
        message = "Employee Not Found",
        response = ErrorDetail.class)})
    @GetMapping(value = "/employee/{employeeid}")
    public ResponseEntity<?> getEmployeeById(
        @ApiParam(value = "Employee id",
            required = true,
            example = "4")
        @PathVariable
            long employeeid)
    {
        Employee e = employeeService.findEmployeeById(employeeid);
        return new ResponseEntity<>(e, HttpStatus.OK);
    }

    @GetMapping(value = "/employeename/{subname}")
    public ResponseEntity<?> listEmployeesWithName(
        @PathVariable
            String subname)
    {
        List<Employee> myEmployees = employeeService.findEmployeeNameContaining(subname);
        return new ResponseEntity<>(myEmployees,
            HttpStatus.OK);
    }

    @GetMapping(value = "/employeeemail/{subemail}")
    public ResponseEntity<?> listEmployeesWithEmail(
        @PathVariable
            String subemail)
    {
        List<Employee> myEmployees = employeeService.findEmployeeEmailContaining(subemail);
        return new ResponseEntity<>(myEmployees,
            HttpStatus.OK);
    }

    @PostMapping(value = "/employee",
        consumes = {"application/json"})
    public ResponseEntity<?> addNewEmployee(
        @Valid
        @RequestBody
            Employee newEmployee)
    {
        // ids are not recognized by the Post method
        newEmployee.setEmployeeid(0);
        newEmployee = employeeService.save(newEmployee);

        // set the location header for the newly created resource
        HttpHeaders responseHeaders = new HttpHeaders();
        URI newEmployeeURI = ServletUriComponentsBuilder.fromCurrentRequest()
            .path("/{employeeid}")
            .buildAndExpand(newEmployee.getEmployeeid())
            .toUri();
        responseHeaders.setLocation(newEmployeeURI);

        return new ResponseEntity<>(null,
            responseHeaders,
            HttpStatus.CREATED);
    }


    @ApiOperation(value = "updates an employee given in the request body",
        response = Void.class)
    @ApiResponses(value = {@ApiResponse(code = 200,
        message = "Employee Found",
        response = Void.class), @ApiResponse(code = 404,
        message = "Employeer Not Found",
        response = ErrorDetail.class)})
    @PutMapping(value = "/employee/{employeeid}",
        consumes = {"application/json"})
    public ResponseEntity<?> updateFullEmployee(
        @Valid
        @ApiParam(value = "a full employee object",
            required = true)
        @RequestBody
            Employee updateEmployee,
        @ApiParam(value = "employeeid",
            required = true,
            example = "4")
        @PathVariable
            long employeeid)
    {
        updateEmployee.setEmployeeid(employeeid);
        employeeService.save(updateEmployee);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PatchMapping(value = "/employee/{employeeid}",
        consumes = {"application/json"})
    public ResponseEntity<?> updateEmployee(
        @RequestBody
            Employee updateEmployee,
        @PathVariable
            long employeeid)
    {
        employeeService.update(updateEmployee,
            employeeid);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping(value = "/employee/{employeeid}")
    public ResponseEntity<?> deleteEmployeeById(
        @PathVariable
            long employeeid)
    {
        employeeService.delete(employeeid);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
