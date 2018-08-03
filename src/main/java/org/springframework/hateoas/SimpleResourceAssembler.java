package org.springframework.hateoas;

import java.util.ArrayList;
import java.util.List;

public class SimpleResourceAssembler<T> implements ResourceAssembler<T, Resource<T>>, ResourcesAssembler<T, Resources<T>> {
  @Override
  public Resource<T> toResource(T entity) {
    Resource<T> resource = new Resource<>(entity);
    addLinks(resource);
    return resource;
  }

  protected void addLinks(Resource<T> resource) {
    // default implementation that adds no links
  }

  @Override
  public Resources<Resource<T>> toResources(Iterable<? extends T> entities) {
    List<Resource<T>> rawResources = new ArrayList<>();
    for (T entitiy : entities) {
      rawResources.add(toResource(entitiy));
    }
    Resources<Resource<T>> resources = new Resources<>(rawResources);
    addLinks(resources);
    return resources;
  }

  protected void addLinks(Resources<Resource<T>> resources) {
    // default implementation that adds no links
  }
}
