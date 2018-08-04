package com.example.springhateoasserver;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.Identifiable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.Optional;

@Data
@Entity
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class Employee implements Identifiable<Long> {
  @Id
  @GeneratedValue
  private Long id;
  private String firstName;
  private String lastName;
  private String role;

  public Employee(String firstName, String lastName, String role) {
    this.firstName = firstName;
    this.lastName = lastName;
    this.role = role;
  }

  public Optional<Long> getId() {
    return Optional.ofNullable(id);
  }
}
