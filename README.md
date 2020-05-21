# Java Swagger Sample Employee Data

## Introduction

This project is used to introduce custom Swagger Documentations to Java Spring REST API CRUD applications. As such it is a small application showing just the code that is needed to explain the topic.

## Database layout

The table layouts are as follows:

- Employee is the driving table
- Email has a Many-To-One relationship with Employee. Each employee has many emails. Each email has only one employee
- Jobtitles has a Many-To-Many relationship with Employee
- EmployeeTitles is the join table to represent the Many-To_Many relationship between Employee and JobTitles

![Sample Employees Database Layout](sampleemps-db.png)

Two different applications exist

- swaggersampleemps-initial - a starting application
- swaggersampleemps - adds custom Swagger documentation to the initial application
