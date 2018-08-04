package com.example.springhateoasserver;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;

import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(EmployeeController.class)
@Import({EmployeeResourceAssembler.class, HypermediaConfiguration.class})
public class EmployeeControllerTest {
  private static final String HAL_FORMS_JSON_UTF8_VALUE = MediaTypes.HAL_FORMS_JSON_VALUE + ";charset=UTF-8";

  @Autowired
  private MockMvc mockMvc;
  @Autowired
  private ObjectMapper objectMapper;
  @MockBean
  private EmployeeRepository employeeRepository;

  @Test
  public void getShouldFetchHalDocuments() throws Exception {
    given(employeeRepository.findAll()).willReturn(
        Arrays.asList(
            new Employee(1L, "Frodo", "Baggins", "ring bearer"),
            new Employee(2L, "Bilbo", "Baggins", "burglar")
        )
    );

    mockMvc.perform(get("/employees"))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(header().string(HttpHeaders.CONTENT_TYPE, HAL_FORMS_JSON_UTF8_VALUE))
        .andExpect(jsonPath("$._links.self.href", endsWith("/employees")))
        .andExpect(jsonPath("$._embedded.employees[0].id", is(1)));
  }

  @Test
  public void postShouldAddUserAndFetchNewDocument() throws Exception {
    Employee employee = new Employee(1L, "Bilbo", "Baggins", "ring bearer");
    given(employeeRepository.save(any(Employee.class))).willReturn(employee);

    mockMvc.perform(post("/employees")
        .contentType(MediaType.APPLICATION_JSON_UTF8)
        .content(objectMapper.writeValueAsString(employee))
    )
        .andDo(print())
        .andExpect(status().isCreated())
        .andExpect(header().string(HttpHeaders.CONTENT_TYPE, HAL_FORMS_JSON_UTF8_VALUE))
        .andExpect(jsonPath("$._links.self.href", endsWith("/employees/" + employee.getId().get())));
  }

  @Test
  public void getShouldFetchHalFormDocuments() throws Exception {
    mockMvc.perform(get("/employees").accept(MediaTypes.HAL_FORMS_JSON))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(header().string(HttpHeaders.CONTENT_TYPE, HAL_FORMS_JSON_UTF8_VALUE))
        .andExpect(jsonPath("$._links.self.href", endsWith("/employees")))
        .andExpect(jsonPath("$._templates.default.method", is("post")))
        .andExpect(jsonPath("$._templates.default.properties[0].name", is("firstName")))
        .andExpect(jsonPath("$._templates.default.properties[0].required", is(true)))
        .andExpect(jsonPath("$._templates.default.properties[1].name", is("id")))
        .andExpect(jsonPath("$._templates.default.properties[1].required", is(true)))
        .andExpect(jsonPath("$._templates.default.properties[2].name", is("lastName")))
        .andExpect(jsonPath("$._templates.default.properties[2].required", is(true)))
        .andExpect(jsonPath("$._templates.default.properties[3].name", is("role")))
        .andExpect(jsonPath("$._templates.default.properties[3].required", is(true)));
  }
}
