package com.example.springhateoasserver;

import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.hateoas.SimpleResourceAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.afford;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@Component
public class EmployeeResourceAssembler extends SimpleResourceAssembler<Employee> {
  @Override
  protected void addLinks(Resource<Employee> resource) {
    resource.add(
        linkTo(methodOn(EmployeeController.class).findOne(resource.getContent().getId().get())).withSelfRel()
    );
  }

  @Override
  protected void addLinks(Resources<Resource<Employee>> resources) {
    resources.add(
        linkTo(methodOn(EmployeeController.class).findAll())
            .withSelfRel().withTitle("retrieve all employees")
            .andAffordance(
                afford(methodOn(EmployeeController.class).newEmployee(null))
            )
    );
  }
}
