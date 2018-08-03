package com.example.springhateoasserver;

import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.hateoas.SimpleResourceAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@Component
public class EmployeeResourceAssembler extends SimpleResourceAssembler<Employee> {
  @Override
  protected void addLinks(Resource<Employee> resource) {
    super.addLinks(resource);
    resource.add(linkTo(methodOn(EmployeeController.class).findOne(resource.getContent().getId())).withSelfRel());
    resource.add(linkTo(methodOn(EmployeeController.class).findAll()).withRel("employees"));
  }

  @Override
  protected void addLinks(Resources<Resource<Employee>> resources) {
    super.addLinks(resources);
    resources.add(linkTo(methodOn(EmployeeController.class).findAll()).withSelfRel());
  }
}
