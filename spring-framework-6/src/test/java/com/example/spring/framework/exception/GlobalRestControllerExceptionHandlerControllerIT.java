package com.example.spring.framework.exception;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootTest
class GlobalRestControllerExceptionHandlerControllerIT {

  @TestConfiguration
  static class TestConfig {
    @RestController
    static class TestRestController {

      @GetMapping("/exception/unsupported-operation")
      public String unsupportedOperation() {
        throw new UnsupportedOperationException("Unsupported");
      }
    }

  }

  @Autowired
  private GlobalRestControllerExceptionHandlerControllerIT.TestConfig.TestRestController controller;

  @Test
  void handleUnsupportedOperation() {
    assertThatThrownBy(() -> controller.unsupportedOperation())
        .isInstanceOf(UnsupportedOperationException.class);
  }
}