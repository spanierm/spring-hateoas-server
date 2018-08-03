package com.example.springhateoasserver;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.hateoas.core.EvoInflectorRelProvider;

@SpringBootApplication
public class SpringHateoasServerApplication {
  public static void main(String[] args) {
    SpringApplication.run(SpringHateoasServerApplication.class, args);
  }

  // used to provide English plurals instead of the default "List" suffix
  @Bean
  EvoInflectorRelProvider relProvider() {
    return new EvoInflectorRelProvider();
  }

//  @Bean
  public CommandLineRunner init(EmployeeRepository employeeRepository) {
    return args -> {
      employeeRepository.save(new Employee("Frodo", "Baggins", "ring bearer"));
      employeeRepository.save(new Employee("Bilbo", "Baggins", "burglar"));
    };
  }
}
