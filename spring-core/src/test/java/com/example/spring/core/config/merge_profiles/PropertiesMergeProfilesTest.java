package com.example.spring.core.config.merge_profiles;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.task.TaskExecutor;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest(properties = "debug=true")
class PropertiesMergeProfilesTest {

  @Configuration
  @EnableAutoConfiguration
  @Import({Config1.class, Config2.class})
  static class Config {

  }

  @EnableAutoConfiguration
  @TestPropertySource(properties = {
      "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration"
  })
  static class Config1 {

  }

  @EnableAutoConfiguration
  @TestPropertySource(properties = {
      "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.task.TaskExecutionAutoConfiguration"
  })
  static class Config2 {

  }

  @Autowired(required = false)
  ObjectMapper objectMapper;
  @Autowired(required = false)
  TaskExecutor taskExecutor;

  @Test
  void test() {
    assertThat(objectMapper)
        .isNull();
    assertThat(taskExecutor)
        .isNull();
  }

}
