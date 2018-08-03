package org.springframework.hateoas;

import lombok.Getter;
import lombok.Setter;
import org.springframework.core.GenericTypeResolver;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;

public class SimpleIdentifiableResourceAssembler<T extends Identifiable<?>> extends SimpleResourceAssembler<T> {
  private final Class<?> controllerClass;
  private final RelProvider relProvider;
  private final Class<?> resourceType;

  @Getter
  @Setter
  private String basePath = "";

  public SimpleIdentifiableResourceAssembler(Class<?> controllerClass, RelProvider relProvider) {
    this.controllerClass = controllerClass;
    this.relProvider = relProvider;
    resourceType = GenericTypeResolver.resolveTypeArgument(this.getClass(), SimpleResourceAssembler.class);
  }

  @Override
  protected void addLinks(Resource<T> resource) {
    resource.add(
        getCollectionLinkBuilder().slash(resource.getContent()).withSelfRel(),
        getCollectionLinkBuilder().withRel(relProvider.getCollectionResourceRelFor(resourceType))
    );
  }

  @Override
  protected void addLinks(Resources<Resource<T>> resources) {
    resources.add(getCollectionLinkBuilder().withSelfRel());
  }

  private LinkBuilder getCollectionLinkBuilder() {
    ControllerLinkBuilder linkBuilder = ControllerLinkBuilder.linkTo(controllerClass);
    for (String pathComponent : (getPrefix() + relProvider.getCollectionResourceRelFor(resourceType)).split("/")) {
      if (!pathComponent.isEmpty()) {
        linkBuilder = linkBuilder.slash(pathComponent);
      }
    }
    return linkBuilder;
  }

  private String getPrefix() {
    return getBasePath().isEmpty() ? "" : getBasePath() + "/";
  }
}
