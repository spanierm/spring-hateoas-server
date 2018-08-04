package com.example.springhateoasserver;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceSupport;
import org.springframework.hateoas.Resources;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
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

  @GetMapping(value = "/", produces = MediaTypes.HAL_JSON_UTF8_VALUE)
  public ResourceSupport root() {
    ResourceSupport resourceSupport = new ResourceSupport();
    resourceSupport.add(
        linkTo(methodOn(EmployeeController.class).root()).withSelfRel(),
        linkTo(methodOn(EmployeeController.class).findAll()).withRel("employees")
    );
    return resourceSupport;
  }

  @GetMapping(value = "/employees", produces = MediaTypes.HAL_JSON_UTF8_VALUE)
  public ResponseEntity<Resources<Resource<Employee>>> findAll() {
    return ResponseEntity.ok(employeeResourceAssembler.toResources(employeeRepository.findAll()));
  }

  @GetMapping(value = "/employees/{id}", produces = MediaTypes.HAL_JSON_UTF8_VALUE)
  public ResponseEntity<Resource<Employee>> findOne(@PathVariable long id) {
    return employeeRepository.findById(id)
        .map(employeeResourceAssembler::toResource)
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
  }

  @PostMapping(value = "/employees", produces = MediaTypes.HAL_JSON_UTF8_VALUE)
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
}
