package com.example.springhateoasserver;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceSupport;
import org.springframework.hateoas.Resources;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.net.URISyntaxException;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@RestController
public class EmployeeController {
  private final EmployeeRepository employeeRepository;
  private final EmployeeResourceAssembler employeeResourceAssembler;

  public EmployeeController(EmployeeRepository employeeRepository, EmployeeResourceAssembler employeeResourceAssembler) {
    this.employeeRepository = employeeRepository;
    this.employeeResourceAssembler = employeeResourceAssembler;
  }

  @GetMapping("/")
  public ResourceSupport root() {
    ResourceSupport resourceSupport = new ResourceSupport();
    resourceSupport.add(
        linkTo(methodOn(EmployeeController.class).root()).withSelfRel(),
        linkTo(methodOn(EmployeeController.class).findAll()).withRel("employees")
    );
    return resourceSupport;
  }

  @GetMapping("/employees")
  public ResponseEntity<Resources<Resource<Employee>>> findAll() {
    return ResponseEntity.ok(employeeResourceAssembler.toResources(employeeRepository.findAll()));
  }

  @PostMapping("/employees")
  public ResponseEntity<?> newEmployee(@RequestBody Employee employee) {
    Employee savedEmployee = employeeRepository.save(employee);
    Resource<Employee> employeeResource = employeeResourceAssembler.toResource(savedEmployee);
    try {
      return ResponseEntity.created(new URI(employeeResource.getRequiredLink(Link.REL_SELF).getHref()))
          .body(employeeResource);
    }
    catch (URISyntaxException e) {
      return ResponseEntity.badRequest().body("Unable to create " + employee);
    }
  }

  @GetMapping("/employees/{id}")
  public ResponseEntity<Resource<Employee>> findOne(@PathVariable long id) {
    return employeeRepository.findById(id)
        .map(employeeResourceAssembler::toResource)
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
  }

  @PutMapping("/employees/{id}")
  public ResponseEntity<?> updateEmployee(@PathVariable long id, @RequestBody Employee employee) {
    if (employee.getId().isPresent() && employee.getId().get() != id) {
      return ResponseEntity.badRequest().body(
          String.format("Id of employee '%d' and id path variable '%d' do not match", employee.getId().get(), id)
      );
    }
    if (!employeeRepository.findById(id).isPresent()) {
      return ResponseEntity.badRequest().body(
          String.format("There is no employee with the id '%d'", id)
      );
    }
    Employee savedEmployee = employeeRepository.save(employee);
    Resource<Employee> employeeResource = employeeResourceAssembler.toResource(savedEmployee);
    try {
      return ResponseEntity.noContent().location(new URI(employeeResource.getRequiredLink(Link.REL_SELF).getHref())).build();
    }
    catch (URISyntaxException e) {
      return ResponseEntity.badRequest().body("Unable to update " + employee);
    }
  }

  @DeleteMapping("/employees/{id}")
  public ResponseEntity<?> deleteEmployee(@PathVariable long id) {
    employeeRepository.deleteById(id);
    return ResponseEntity.noContent().build();
  }
}
