package com.example.spring.data.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@Slf4j
class UserRepositoryRestResourceIT {

  @Autowired
  MockMvc mockMvc;
  @Autowired
  ObjectMapper objectMapper;

  @Test
  void findAll() throws Exception {
    var response = mockMvc.perform(get("/users"))
        .andDo(print())
        .andExpect(status().is2xxSuccessful())
        .andExpect(jsonPath("$._embedded.users").isNotEmpty())
        .andExpect(jsonPath("$._embedded.users").isArray())
        .andExpect(jsonPath("$._embedded.users.length()")
            .value(Matchers.greaterThan(2), Integer.class))
        .andExpect(jsonPath("$._links").isNotEmpty())
        .andReturn()
        .getResponse();

    String usersJson = response.getContentAsString();
    assertThat(usersJson).isNotEmpty();
  }

}