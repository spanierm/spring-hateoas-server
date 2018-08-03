package org.springframework.hateoas;

public interface ResourcesAssembler<T, D extends ResourceSupport> {
  Resources toResources(Iterable<? extends T> entities);
}
