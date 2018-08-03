package com.example.springhateoasserver;

import org.springframework.hateoas.RelProvider;
import org.springframework.hateoas.SimpleIdentifiableResourceAssembler;
import org.springframework.stereotype.Component;

@Component
public class EmployeeResourceAssembler extends SimpleIdentifiableResourceAssembler<Employee> {
  public EmployeeResourceAssembler(RelProvider relProvider) {
    super(EmployeeController.class, relProvider);
  }
}
