package com.example.spring.framework.api;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.client.RestTestClient;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@WebMvcTest(controllers = TestsHelloController.class)
@AutoConfigureRestTestClient
class RestTestClientTest {

  @Autowired
  RestTestClient restTestClient;
  @Autowired
  MockMvc mockMvc;

  @Test
  void test() {
    restTestClient.get().uri("/tests/hello")
        .exchange()
        .expectStatus().isOk()
        .expectBody(String.class).isEqualTo("Hello World!");
  }

  @Test
  void mockMvc_test() throws Exception {
    mockMvc.perform(MockMvcRequestBuilders.get("/tests/hello"))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(content().string("Hello World!"));
  }

}
