package com.lambdaschool.sampleemps.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.ArrayList;
import java.util.List;

@ApiModel(value = "Employee",
    description = "Yes an actual employee record")
@Entity
@Table(name = "employees")
@JsonIgnoreProperties(value = {"hasvalueforsalary"})
public class Employee
{
    @ApiModelProperty(name = "employee id",
        value = "primary key for employee",
        required = true,
        example = "1")
    @Id // The primary key
    @GeneratedValue(strategy = GenerationType.AUTO) // We will let the database decide how to generate it
    private long employeeid; // long so we can have many rows

    @ApiModelProperty(name = "employee name",
        value = "full name of employee",
        required = true,
        example = "Best Employee")
    @Column(nullable = false,
        unique = true)
    private String name;

    /**
     * Used to determine if the field salary has been set or is NULL.
     */
    // Note transient values do not get Swagger documentation!
    @Transient
    public boolean hasvalueforsalary = false;

    @ApiModelProperty(name = "employee name",
        value = "salary of employee",
        required = false,
        example = "100000")
    private double salary;

    @ManyToMany()
    /*
     * Note: JoinTable is the name of a table that will get created in the database combining the two primary keys making up this relationship
     *       joinColumn is the primary key of the main, driving, table
     *       inverseJoinColumns is the primary key of the other table in the relationship
     */
    @JoinTable(name = "employeetitles",
        joinColumns = @JoinColumn(name = "employeeid"),
        inverseJoinColumns = @JoinColumn(name = "jobtitleid"))
    @JsonIgnoreProperties(value = "employees",
        allowSetters = true)
    List<JobTitle> jobtitles = new ArrayList<>();

    @OneToMany(mappedBy = "employee",
        cascade = CascadeType.ALL,
        // when adding, reading, updating, and delete, the operations should affect the emails table as well)
        orphanRemoval = true)
    // if we find a email that has a reference to an employee that does not exist, delete that email record
    @JsonIgnoreProperties(value = "employee",
        allowSetters = true)
    private List<Email> emails = new ArrayList<>();

    public Employee()
    {
        // the default constructor is required by the JPA
    }

    public long getEmployeeid()
    {
        return employeeid;
    }

    public void setEmployeeid(long employeeid)
    {
        this.employeeid = employeeid;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public double getSalary()
    {
        return salary;
    }

    public void setSalary(double salary)
    {
        hasvalueforsalary = true;
        this.salary = salary;
    }

    public List<Email> getEmails()
    {
        return emails;
    }

    public void setEmails(List<Email> emails)
    {
        this.emails = emails;
    }

    public List<JobTitle> getJobtitles()
    {
        return jobtitles;
    }

    public void setJobtitles(List<JobTitle> jobtitles)
    {
        this.jobtitles = jobtitles;
    }

    public void addJobTitle(JobTitle jt)
    {
        jobtitles.add(jt);
        jt.getEmployees()
            .add(this);
    }

    public void removeJobTitle(JobTitle jt)
    {
        jobtitles.remove(jt);
        jt.getEmployees()
            .remove(this);
    }
}