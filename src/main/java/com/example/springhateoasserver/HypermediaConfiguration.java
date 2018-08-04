package com.example.springhateoasserver;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.hateoas.config.EnableHypermediaSupport;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

@Configuration
@EnableHypermediaSupport(type = EnableHypermediaSupport.HypermediaType.HAL_FORMS)
public class HypermediaConfiguration {
  @Bean
  public static HaloObjectMapperConfigurer haloObjectMapperConfigurer() {
    return new HaloObjectMapperConfigurer();
  }

  private static class HaloObjectMapperConfigurer implements BeanPostProcessor, BeanFactoryAware {
    private BeanFactory beanFactory;

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
      if (bean instanceof ObjectMapper && beanName.startsWith("_hal") && beanName.endsWith("Mapper")) {
        postProcessHalObject((ObjectMapper) bean);
      }
      return bean;
    }

    private void postProcessHalObject(ObjectMapper objectMapper) {
      try {
        Jackson2ObjectMapperBuilder builder = beanFactory.getBean(Jackson2ObjectMapperBuilder.class);
        builder.configure(objectMapper);
      }
      catch (NoSuchBeanDefinitionException ignored) {
      }
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
      return bean;
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
      this.beanFactory = beanFactory;
    }
  }
}
