package com.example.spring.webstack.web.api;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = GreetingController.class)
class MockMvcTest {

  @Autowired
  MockMvc mockMvc;

  @Test
  void test() throws Exception {
    mockMvc.perform(get("/hello"))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(content().string("Hello, WebMvc Spring!"));
  }
}
