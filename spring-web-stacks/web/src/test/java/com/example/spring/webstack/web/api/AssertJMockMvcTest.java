package com.example.spring.webstack.web.api;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.assertj.MockMvcTester;

@WebMvcTest(controllers = GreetingController.class)
class AssertJMockMvcTest {

  @Autowired
  MockMvcTester mockMvc;

  @Test
  void test() {
    assertThat(mockMvc.get().uri("/hello"))
        .debug()
        .hasStatusOk()
        .bodyText().contains("Hello, WebMvc Spring!");
  }

}
