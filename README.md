>Put custom Swagger documentation on the following items:

**Models**
<dl>
  <dt>At the table level including</dt>
  <dd>Name</dd>
  <dd>Description</dd>
  <dt>At the column, field, or property level including</dt>
  <dd>Name</dd>
  <dd>Description (called value)</dd>
  <dd>Required or not</dd>
  <dd>Giving an example</dd>
  <dt>Controller</dt>
  </dl>
```java
  Operation’s Level
    Description (called value)
    Response type (Class type)
  Response container type (optional, usually a List)
    ApiResponse
```
List of status codes
value
Status Code
Custom Message
Response (class)
Parameters
Description (called value)
Required or not
Example (if a base data type or String)
  
Swagger can do much, much more but let’s stick with the basics for now. Remember the end result is to provide documentation to your clients on how to use your API!

Follow Along
Let’s add some custom Swagger documentation to an existing application.

Open the application java-sampleswagger-initial from the GitHub repository https://github.com/LambdaSchool/java-sampleswagger.git. This application is similar to the application from https://github.com/LambdaSchool/java-sampleemps.git/sampleemps_data_modeling with the addition of find employee by id endpoints and associated code. I also added the ErrorDetail and ValidationError classes to show how you can customize Swagger when you have full exception handling in place.

Start with the defaults
First, we need to add the dependencies and configuration for the default Swagger documentation.

Add the following Swagger Dependencies to the pom.xml file

        <!-- Swagger Dependencies Start -->
        <!-- https://mvnrepository.com/artifact/io.springfox/springfox-swagger2 -->
        <dependency>
            <groupId>io.springfox</groupId>
            <artifactId>springfox-swagger2</artifactId>
            <version>2.9.2</version>
        </dependency>

        <!-- https://mvnrepository.com/artifact/io.springfox/springfox-swagger-ui -->
        <dependency>
            <groupId>io.springfox</groupId>
            <artifactId>springfox-swagger-ui</artifactId>
            <version>2.9.2</version>
        </dependency>

        <dependency>
            <groupId>io.springfox</groupId>
            <artifactId>springfox-bean-validators</artifactId>
            <version>2.9.2</version>
        </dependency>
        <!-- Swagger Dependencies End -->
Yes, I did throw in an additional dependency. The springfox-bean-validator allows Swagger to recognize some of the validation annotations and document their messages as well!

Now we need to add the configuration file for Swagger. This is similar to the configuration file we added before. However, notice the addition of the annotation @Import(BeanValidatorPluginsConfiguration.class). Again, this always Swagger to work with the validation annotations. Feel free to put in your own contact information! Remember in adding all of this code, you will need to make sure the proper imports are done!

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import springfox.bean.validators.configuration.BeanValidatorPluginsConfiguration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
@Import(BeanValidatorPluginsConfiguration.class)
public class Swagger2Config
{
    @Bean
    public Docket api()
    {
        return new Docket(DocumentationType.SWAGGER_2)
            .select()
            .apis(RequestHandlerSelectors
                .basePackage("com.lambdaschool.sampleemps"))
            .paths(PathSelectors.regex("/.*"))
            .build()
            .apiInfo(apiEndPointsInfo());
    }

    private ApiInfo apiEndPointsInfo()
    {
        return new ApiInfoBuilder().title("Custom Swagger Documentation Example")
            .description("Custom Swagger Documentation Example")
            .contact(new Contact("John Mitchell",
                "http://www.lambdaschool.com",
                "john@lambdaschool.com"))
            .license("MIT")
            .licenseUrl("https://github.com/LambdaSchool/java-sampleswagger/blob/master/LICENSE")
            .version("1.0.0")
            .build();
    }
Document the Models
Now let’s add custom Swagger documentation to the Employee Model Let’s describe the model itself. Right before the class header, add the following annotation. Note that value is the name of the model or table and description tells us what is actually being modeled.

@ApiModel(value = "Employee",
    description = "Yes an actual employee record")
// class header and following code
For any property in any entity that needs custom documentation, you use the @ApiModelProperty annotation. So, for each field in the Employee model, add the following annotation adjusting the information to fit the property. This annotation goes right before the field declaration. For example, this is the annotation for the primary key. Note that here description is called value and example always takes a String value.

    @ApiModelProperty(name = "employee id",
        value = "primary key for employee",
        required = true,
        example = "1")
// field declaration
For another example, this is the annotation for the employee name:

    @ApiModelProperty(name = "employee name",
        value = "full name of employee",
        required = true,
        example = "Best Employee")
// field declaration
A Documented Employee Model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
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
public class Employee extends Auditable
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

    @Transient
    public boolean hasvalueforsalary = false;

    @ApiModelProperty(name = "employee name",
            value = "salary of employee",
            required = false,
            example = "100000")
    private double salary;

    /*
     * emp is the field from EmployeeTitles
     * CascadeType.ALL says that when we add, update, delete an Employee record, have that affect emp in EmployeeTitle.
     * Notice that in EmployeeTitle there is no cascade option. This way manipulating an Employee record only affects
     * the EmployeeTitle join table but does not affect the JobTitle table.
     */
    @OneToMany(mappedBy = "emp",
        cascade = CascadeType.ALL)
    /*
     * When displaying EmployeeTitles from the Employee class, do not display the employee again.
     * However do allow for data to be added to the emp field in EmployeeTitles
     */
    @JsonIgnoreProperties(value = "emp",
        allowSetters = true)
    /*
     * We know all of this works with EmployeeTitles because that is the class of the field name that making the One To Many relationship!
     * This array contains the list of EmployeeTitles assigned to this Employee
     */
    private List<EmployeeTitles> jobnames = new ArrayList<>();

    /*
     * This starts the One To Many relation of employee to emails
     */
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

    public Employee(
        String name,
        double salary,
        List<EmployeeTitles> jobnames)
    {
        this.name = name;
        this.salary = salary;

        /*
         * Force the list of roles to be associated with this new employee object!
         */
        for (EmployeeTitles et : jobnames)
        {
            et.setEmp(this);
        }
        this.jobnames = jobnames;
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

    /*
     * We need a getter for the new One To Many relations
     */
    public List<EmployeeTitles> getJobnames()
    {
        return jobnames;
    }

    /*
     * We need a setter for the new One To Many relations
     */
    public void setJobnames(List<EmployeeTitles> jobnames)
    {
        this.jobnames = jobnames;
    }

    /*
     * Due to the new One To Many relation, we need a new way to add a Job Title to the employee
     */
    public void addJobTitle(
        JobTitle jobTitle,
        String manager)
    {
        jobnames.add(new EmployeeTitles(this,
            jobTitle,
            manager));
    }
}
Document the controllers
Now let’s add custom Swagger documentation to the EmployeeControl, first adding annotations to the list all employees endpoint. All we need for the list all employees endpoint is the @ApiOperation with a value, description, a response class, and a response container class. So add the following annotation before the listAllEmployees() method

    @ApiOperation(value = "returns all Employees",
        response = Employee.class,
        responseContainer = "List")
    // method header
Now let’s look at one slightly more involved - return a single employee based off an employee id. Here we are adding

ApiOperation to explain what is happening and any response types.
@ApiResponses to document how you are responding to exceptions. This only makes sense if you have full exception handling in place in your application. Otherwise I would leave this annotation out and just use the Swagger defaults for ApiResponses.
@ApiParam is needed before each parameter in the method header! Here we just have one Parameter, the employee id we are seeking. Even if the parameter is coming in through the request body, we still annotation the parameter.
Add the following annotations to your employee controller for the method getEmployeeById.

    @ApiOperation(value = "Retrieve an employee based off of employee id",
        response = Employee.class)
    @ApiResponses(value = {@ApiResponse(code = 200,
        message = "Employee Found",
        response = Employee.class), @ApiResponse(code = 404,
        message = "Employee Not Found",
        response = ErrorDetail.class)})
    // method header
    //...
        @ApiParam(value = "Employee id",
            required = true,
            example = "4")
    // @PathVariable and following
And let’s do one more. Let’s document the PUT employee endpoint. What is different about this endpoint is that nothing is returned in the response body, so we say the response is Void.class Add the following annotation to your code to document the PUT endpoint.

    @ApiOperation(value = "updates an employee given in the request body",
        response = Void.class)
    @ApiResponses(value = {@ApiResponse(code = 200,
        message = "Employee Found",
        response = Void.class), @ApiResponse(code = 404,
        message = "Employeer Not Found",
        response = ErrorDetail.class)})
    // method headers
            @ApiParam(value = "a full employee object",
            required = true)
    //...
        @ApiParam(value = "employeeid",
            required = true,
            example = "4")
    //...
The Employee Controller with Annotations in Place

import com.lambdaschool.sampleemps.models.Employee;
import com.lambdaschool.sampleemps.models.ErrorDetail;
import com.lambdaschool.sampleemps.services.EmployeeService;
import com.lambdaschool.sampleemps.views.EmpNameCountJobs;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
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
        @ApiParam(value = "a full employee object",
                required = true)
        @Valid
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

    @GetMapping(value = "/job/counts")
    public ResponseEntity<?> getEmpJobCounts()
    {
        List<EmpNameCountJobs> myEmployees = employeeService.getEmpNameCountJobs();
        return new ResponseEntity<>(myEmployees,
            HttpStatus.OK);
    }

    @DeleteMapping(value = "/employee/{employeeid}/jobtitle/{jobtitleid}")
    public ResponseEntity<?> deleteEmployeeJobTitlesByid(
        @PathVariable
            long employeeid,
        @PathVariable
            long jobtitleid)
    {
        employeeService.deleteEmpJobTitle(employeeid,
        jobtitleid);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping(value = "/employee/{employeeid}/jobtitle/{jobtitleid}/manager/{manager}")
    public ResponseEntity<?> addEmployeeJobTitlesByid(
        @PathVariable
            long employeeid,
        @PathVariable
            long jobtitleid,
        @PathVariable
            String manager)
    {
        employeeService.addEmpJobTitle(employeeid,
            jobtitleid, manager);

        return new ResponseEntity<>(HttpStatus.OK);
    }
}
Examine our work
Run the application and surf to the site http://localhost:2019/swagger-ui.html.

Expand the information for the Employee Controller and notice your custom documentation is now being used!
Expand the information for the Employee Model and notice that custom documentation is now being used!
Challenge
Your task is to add custom Swagger documentation to the JobTitle Model and to the rest of the end points in the EmployeeController

Dig Deeper
Swagger Homepage
Swagger Homepage
Swagger Complete Example
Swagger and Spring Boot Complete Example
Learn to gather and use data from other APIs

Overview
See the Github Repository https://github.com/LambdaSchool/java-sampleotherapis.git for the code used in the objective.
Software Needed
Java Development Kit (JDK) - at least version 11
JetBrains IntelliJ IDEA IDE
Postman
Often we need to get data from other backend APIs systems. These are either provided by other companies, data scientist people within our own company, or some other group. We become the client. We are reliant on the other system on how to retrieve the data and how the retrieved data is organized. Let’s take a look at a few examples.

We are going to retrieve data from three different APIs that return the data is three common, but very different ways.

Straight JSON
The easiest to work with will be Straight JSON as from the API http://numbersapi.com/random/year?json. This API returns a random math fact based on a random year. Surfing to that site returns

{
    "text": "287 is the year that Emperor Diocletian and Maximian become Roman Consuls.",
    "number": 287,
    "found": true,
    "type": "year"
}
Extra Fields wrapped in a class
Sometimes our data comes back wrapped in a class name and contains fields we do not need. For example, the API http://api.open-notify.org/iss-now.json does this. This API returns the current location of the International Space Station.

{
    "message": "success",
    "iss_position": {
        "latitude": "-46.3184",
        "longitude": "-65.6439"
    },
    "timestamp": 1590079067
}
Wrapped in a class requiring a parameter
Sometimes to get the data we want we have to send a parameter along with our endpoint. This API will return the Klingon translation of the given English phrase. https://api.funtranslations.com/translate/klingon.json?text=”The enemy of my enemy is the enemy I kill last”

{
    "success": {
        "total": 1
    },
    "contents": {
        "translated": "\"The jagh of my jagh is the jagh jih hoh hochdich\"",
        "text": "\"The enemy of my enemy is the enemy I kill last\"",
        "translation": "klingon"
    }
}
For all the data we retrieve we need to create a model that can store the data. This data may or not need to be saved to our database. This is usually accomplished through a separate model class for the data along with a collection to store a list of the data. Let’s see how this works in practice! As we are creating POJOs to hold our JSON objects, do note how the names of the fields must match exactly between the two entities.

Follow Along
Let’s learn about these by following along, coding the following examples. Open the sample-otherapis-initial application from https://github.com/LambdaSchool/java-sampleotherapis.gitt. This application is the same as the sampleemps_data_modeling application from the GitHub Repository https://github.com/LambdaSchool/java-sampleemps.gitt

Straight JSON
We want to report a random fact about a year to the console on application start up. Let’s create a model to hold the data we will get back from the API. Under the models subpackage, create a class called YearFact. This will NOT be saved in our database so we will NOT use the @Entity annotation. We need a field to hold each one of the pieces of data we want to save. We also need an annotation to say to ignore all other pieces of data. We will be using @JsonIgnoreProperties(ignoreUnknown = true)

The API endpoint we are accessing is http://numbersapi.com/random/year?jsonn
Looking at the JSON that is returned by this endpoint we only want the data text and number. See JSON object from above.
So enter the following code for the class YearFact. Remember in adding all of this code, you will need to make sure the proper imports are done! Do note how closely this matches the layout of the JSON object!

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

// needed to ignore any fields coming across that we do not want in our final class.
@JsonIgnoreProperties(ignoreUnknown = true)
public class YearFact
{
    private String text;
    private int number;

    public String getText()
    {
        return text;
    }

    public void setText(String text)
    {
        this.text = text;
    }

    public int getNumber()
    {
        return number;
    }

    public void setNumber(int number)
    {
        this.number = number;
    }

    @Override
    public String toString()
    {
        return "YearFact{" +
                "text='" + text + '\'' +
                ", number=" + number +
                '}';
    }
}
We now need to go get the data and then display it in the console. Add the following code to the main method in the SampleempsApplication class. In this code we introduce

The RestTemplate - creates the object that is needed to do a client side Rest API call.
MappingJackson2HttpMessageConverter - a way to configure our rest request.
Doing a request now instead of responding to one.
        /*
         * Creates the object that is needed to do a client side Rest API call.
         * We are the client getting data from a remote API.
         */
        RestTemplate restTemplate = new RestTemplate();

        // we need to tell our RestTemplate what format to expect
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        // a couple of common formats
        // converter.setSupportedMediaTypes(Collections.singletonList(MediaType.TEXT_HTML));
        // converter.setSupportedMediaTypes(Collections.singletonList(MediaType.APPLICATION_JSON));
        // or we can accept all formats! Easiest but least secure
        converter.setSupportedMediaTypes(Collections.singletonList(MediaType.ALL));
        restTemplate.getMessageConverters().add(converter);

        // create the url to access the API
        String requestURL = "http://numbersapi.com/random/year?json";
        // create the responseType expected. Notice the YearFact is the data type we are expecting back from the API!
        ParameterizedTypeReference<YearFact> responseType = new ParameterizedTypeReference<>()
        {
        };
        // create the response entity. do the get and get back information
        ResponseEntity<YearFact> responseEntity = restTemplate.exchange(requestURL,
                HttpMethod.GET,
                null,
                responseType);
        // now that we have our data, let's print it to the console!
        YearFact ourYearFact = responseEntity.getBody();
        System.out.println(ourYearFact);
So we end up with the following SampleempsApplication class.

import com.lambdaschool.sampleemps.models.YearFact;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;

@EnableJpaAuditing
@SpringBootApplication
public class SampleempsApplication
{

    public static void main(String[] args)
    {
        /*
         * Creates the object that is needed to do a client side Rest API call.
         * We are the client getting data from a remote API.
         */
        RestTemplate restTemplate = new RestTemplate();

        // we need to tell our RestTemplate what format to expect
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        // a couple of common formats
        // converter.setSupportedMediaTypes(Collections.singletonList(MediaType.TEXT_HTML));
        // converter.setSupportedMediaTypes(Collections.singletonList(MediaType.APPLICATION_JSON));
        // or we can accept all formats! Easiest but least secure
        converter.setSupportedMediaTypes(Collections.singletonList(MediaType.ALL));
        restTemplate.getMessageConverters().add(converter);

        // create the url to access the API
        String requestURL = "http://numbersapi.com/random/year?json";
        // create the responseType expected. Notice the YearFact is the data type we are expecting back from the API!
        ParameterizedTypeReference<YearFact> responseType = new ParameterizedTypeReference<>()
        {
        };
        // create the response entity. do the get and get back information
        ResponseEntity<YearFact> responseEntity = restTemplate.exchange(requestURL,
                HttpMethod.GET,
                null,
                responseType);
        // now that we have our data, let's print it to the console!
        YearFact ourYearFact = responseEntity.getBody();
        System.out.println(ourYearFact);

        SpringApplication.run(SampleempsApplication.class,
            args);
    }
}
Run the application and notice something similar in the console!

14:38:16.213 [main] DEBUG org.springframework.web.client.RestTemplate - HTTP GET http://numbersapi.com/random/year?json
14:38:16.242 [main] DEBUG org.springframework.web.client.RestTemplate - Accept=[application/json, application/*+json, */*]
14:38:23.788 [main] DEBUG org.springframework.web.client.RestTemplate - Response 200 OK
14:38:23.791 [main] DEBUG org.springframework.web.client.RestTemplate - Reading to [com.lambdaschool.sampleemps.models.YearFact]
YearFact{text='1889 is the year that International Workers Congresses of Paris.', number=1889}
14:38:23.899 [restartedMain] DEBUG org.springframework.web.client.RestTemplate - HTTP GET http://numbersapi.com/random/year?json
14:38:23.901 [restartedMain] DEBUG org.springframework.web.client.RestTemplate - Accept=[application/json, application/*+json, */*]
14:38:23.981 [restartedMain] DEBUG org.springframework.web.client.RestTemplate - Response 200 OK
14:38:23.982 [restartedMain] DEBUG org.springframework.web.client.RestTemplate - Reading to [com.lambdaschool.sampleemps.models.YearFact]
YearFact{text='1366 is the year that Muhammed V builds the Granada Hospital in Granada (in present-day Spain).', number=1366}
Extra Fields wrapped in a class
We want an endpoint that will return the current position of the international space station.

Create a model to hold the ISS Position data. We only want the fields latitude and longitude which are part of the iss_position class embedded in the JSON Object. Let’s create a model to hold the data that is retrieved from the API. This model will include a field that is of some class type. Create a model called IssPositionReturnData in the subpackage models. Add the following code.

Do note how closely this matches the layout of the JSON object!
Do note that we do not need the @JsonIgnoreProperties(ignoreUnknown = true) as we are handling all incoming pieces of data. We not returning all that data to the client but we have a place in our application to store all data retrieved from the API.
Note that the field of the embedded class type has to match the field name from the JSON Object, just like all the other fields. Nothing special about it being a class!
public class IssPositionReturnData
{
    private String message;
    private IssPosition iss_position;
    private long timestamp;

    public String getMessage()
    {
        return message;
    }

    public void setMessage(String message)
    {
        this.message = message;
    }

    public IssPosition getIss_position()
    {
        return iss_position;
    }

    public void setIss_position(IssPosition iss_position)
    {
        this.iss_position = iss_position;
    }

    public long getTimestamp()
    {
        return timestamp;
    }

    public void setTimestamp(long timestamp)
    {
        this.timestamp = timestamp;
    }
}
We now need to create that embedded class type. Create a model called IssPosition in the subpackage models. Add the following code.

public class IssPosition
{
    private String latitude;
    private String longitude;

    public String getLatitude()
    {
        return latitude;
    }

    public void setLatitude(String latitude)
    {
        this.latitude = latitude;
    }

    public String getLongitude()
    {
        return longitude;
    }

    public void setLongitude(String longitude)
    {
        this.longitude = longitude;
    }
}
Now let’s go retrieve the data. Create a new class under the subpackage controllers called OtherApis. Add the following code.

import com.lambdaschool.sampleemps.models.IssPosition;
import com.lambdaschool.sampleemps.models.IssPositionReturnData;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;

@RestController
@RequestMapping("/otherapis") // optional
public class OtherApis
{
    /*
     * Creates the object that is needed to do a client side Rest API call.
     * We are the client getting data from a remote API.
     * We can share this template among endpoints
     */
    private RestTemplate restTemplate = new RestTemplate();

    @GetMapping(value = "/isspositions")
    public ResponseEntity<?> listIssPositions()
    {
        // we need to tell our RestTemplate what format to expect
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        // a couple of common formats
        // converter.setSupportedMediaTypes(Collections.singletonList(MediaType.TEXT_HTML));
        // converter.setSupportedMediaTypes(Collections.singletonList(MediaType.APPLICATION_JSON));
        // or we can accept all formats! Easiest but least secure
        converter.setSupportedMediaTypes(Collections.singletonList(MediaType.ALL));
        restTemplate.getMessageConverters().add(converter);

        // create the url to access the API
        String requestURL = "http://api.open-notify.org/iss-now.json";
        // create the responseType expected. Notice the IssPositionReturnData is the data type we are expecting back from the API!
        ParameterizedTypeReference<IssPositionReturnData> responseType = new ParameterizedTypeReference<>()
        {
        };

        // create the response entity. do the get and get back information
        ResponseEntity<IssPositionReturnData> responseEntity = restTemplate.exchange(requestURL,
                HttpMethod.GET,
                null,
                responseType);
        // we want to return the Iss_position data. From the data that gets returned in the body,
        // get the Iss_position data only and return it.
        // putting the data into its own object first, prevents the data from being reported to client inside of
        // an embedded. So the response will look more like our clients are use to!
        IssPosition ourIssPosition = responseEntity.getBody().getIss_position();
        return new ResponseEntity<>(ourIssPosition,
                HttpStatus.OK);
    }
}
Run the application and surf to the endpoint http://localhost:2019/otherapis/isspositionss. You get back data similar to the following

{
    "latitude": "38.1104",
    "longitude": "-49.8389"
}
Wrapped in a class requiring a parameter
We want an endpoint that will return the Klingon translation of a phrase coming in through a path variable. We need to create a class that contains a field that is of type contents class. So, we also need to create the contents class. In the models subpackage, create a class called Translation. Add to that the class the following code. Note that the field of the embedded class type has to match the field name from the JSON Object, just like all the other fields. Nothing special about it being a class!

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Translation
{
    private TranslationContents contents;

    public TranslationContents getContents()
    {
        return contents;
    }

    public void setContents(TranslationContents contents)
    {
        this.contents = contents;
    }
}
Now we need to create the embedded class type contents. In the models subpackage, create a class called TranslationContents and add the following code.

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TranslationContents
{
    private String translated;

    public String getTranslated()
    {
        return translated;
    }

    public void setTranslated(String translated)
    {
        this.translated = translated;
    }
}
We will now create the endpoint to access this information. Handling of the path variable is just like we handle all other path variables. Add the following code to the bottom of the controller class OtherApis.

    @GetMapping(value = "/klingon/{englishText}")
    public ResponseEntity<?> getTranslation(
            @PathVariable
                    String englishText)
    {
        // we need to tell our RestTemplate what format to expect
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        // a couple of common formats
        // converter.setSupportedMediaTypes(Collections.singletonList(MediaType.TEXT_HTML));
        // converter.setSupportedMediaTypes(Collections.singletonList(MediaType.APPLICATION_JSON));
        // or we can accept all formats! Easiest but least secure
        converter.setSupportedMediaTypes(Collections.singletonList(MediaType.ALL));
        restTemplate.getMessageConverters()
                .add(converter);

        // create the url to access the API including adding the path variable
        String requestURL = "https://api.funtranslations.com/translate/klingon.json?text=" + englishText;
        // create the responseType expected. Notice the Translation is the data type we are expecting back from the API!
        ParameterizedTypeReference<Translation> responseType = new ParameterizedTypeReference<>()
        {
        };

        // create the response entity. do the get and get back information
        ResponseEntity<Translation> responseEntity = restTemplate.exchange(requestURL,
                HttpMethod.GET,
                null,
                responseType);
        // we want to return the contents of the translation data. From the data that gets returned in the body,
        // get the contents data only and return it.
        // putting the data into its own object first, prevents the data from being reported to client inside of
        // an embedded. So the response will look more like our clients are use to!
        TranslationContents ourTranslation = responseEntity.getBody()
                .getContents();
        return new ResponseEntity<>(ourTranslation,
                HttpStatus.OK);
    }
Surf to the endpoint http://localhost:2019/otherapis/klingon/”Success”” and we get something similar to the following

{
    "translated": "\"qapla'\""
}
Now surf to this endpoint http://localhost:2019/otherapis/klingon/”I want to be a backend developer” and we get something similar to the following

{
    "translated": "\"jih neh to qu' a backend developer\""
}
So go forth and gather data from other APIs!

Dig Deeper
JSON to POJO
Convert from JSON to POJO
Guided Project
java-exceptionalusermodel
We are adding client friendly exception handling.

GitHub Repo
Project
java-schools
Let’s put our new exception(al) skills to work!

Review
Class Recordings
You can use class recordings to help you master the material.

All previous recordings
Demonstrate Mastery
To demonstrate mastery of this module, you need to complete and pass a code review on each of the following:

Objective challenge:
Following the same process you used to create a ResourceNotFoundException, create a ResourceFoundException to be used in place of the built-in exception EntityExistsException.

Objective challenge:
Add an annotation to validate the email field in the Email model. Note that since the word email is already in use in the method, IntelliJ adds the full address of the annotation to give you something like @javax.validation.constraints.Email. It happens when these common words are used. We have to know which one to use at what time!

Objective challenge:
Your task is to add custom Swagger documentation to the JobTitle Model and to the rest of the end points in the EmployeeController

Guided Project: java-exceptionalusermodel
Project: java-schools
