package com.example.springhateoasserver;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@RestController
public class EmployeeController {
  private final EmployeeRepository employeeRepository;

  public EmployeeController(EmployeeRepository employeeRepository) {
    this.employeeRepository = employeeRepository;
  }

  @GetMapping(value = "/employees", produces = MediaTypes.HAL_JSON_UTF8_VALUE)
  public ResponseEntity<Resources<Resource<Employee>>> findAll() {
    List<Resource<Employee>> employees = StreamSupport.stream(employeeRepository.findAll().spliterator(), false)
        .map(employee -> new Resource<>(
            employee,
            linkTo(methodOn(EmployeeController.class).findOne(employee.getId())).withSelfRel(),
            linkTo(methodOn(EmployeeController.class).findAll()).withRel("employees"))
        )
        .collect(Collectors.toList());
    return ResponseEntity.ok(new Resources<>(employees, linkTo(methodOn(EmployeeController.class).findAll()).withSelfRel()));
  }

  @GetMapping(value = "/employees/{id}", produces = MediaTypes.HAL_JSON_UTF8_VALUE)
  public ResponseEntity<Resource<Employee>> findOne(@PathVariable long id) {
    return employeeRepository.findById(id)
        .map(employee -> new Resource<>(
            employee,
            linkTo(methodOn(EmployeeController.class).findOne(employee.getId())).withSelfRel(),
            linkTo(methodOn(EmployeeController.class).findAll()).withRel("employees"))
        )
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
  }

  @PostMapping(value = "/employees", produces = MediaTypes.HAL_JSON_UTF8_VALUE)
  public ResponseEntity<?> newEmployee(@RequestBody Employee employee) {
    Employee savedEmployee = employeeRepository.save(employee);
    Resource<Employee> employeeResource = new Resource<>(
        savedEmployee,
        linkTo(methodOn(EmployeeController.class).findOne(savedEmployee.getId())).withSelfRel()
    );
    try {
      return ResponseEntity.created(new URI(employeeResource.getLink(Link.REL_SELF).getHref()))
          .body(employeeResource);
    }
    catch (URISyntaxException e) {
      return ResponseEntity.badRequest().body("Unable to create " + employee);
    }
  }
}
