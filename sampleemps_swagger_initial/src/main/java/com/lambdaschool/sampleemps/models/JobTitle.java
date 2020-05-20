package com.lambdaschool.sampleemps.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "jobtitles")
public class JobTitle
{
    @Id // The primary key
    @GeneratedValue(strategy = GenerationType.AUTO) // We will let the database decide how to generate it
    private long jobtitleid; // long so we can have many rows

    @Column(nullable = false,
        unique = true)
    private String title;

    @ManyToMany(mappedBy = "jobtitles")
    @JsonIgnoreProperties(value = "jobtitles",
        allowSetters = true)
    private List<Employee> employees = new ArrayList<>();

    public JobTitle()
    {
        // the default constructor is required by the JPA
    }

    public long getJobtitleid()
    {
        return jobtitleid;
    }

    public void setJobtitleid(long jobtitleid)
    {
        this.jobtitleid = jobtitleid;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public List<Employee> getEmployees()
    {
        return employees;
    }

    public void setEmployees(List<Employee> employees)
    {
        this.employees = employees;
    }
}