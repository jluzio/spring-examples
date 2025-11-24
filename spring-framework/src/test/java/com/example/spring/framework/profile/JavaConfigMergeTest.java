package com.example.spring.framework.profile;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.task.TaskExecutionAutoConfiguration;
import org.springframework.boot.jackson.autoconfigure.JacksonAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.task.TaskExecutor;
import tools.jackson.databind.ObjectMapper;

@SpringBootTest(properties = "debug=true")
class JavaConfigMergeTest {

  @Configuration
  @EnableAutoConfiguration
  @Import({Config1.class, Config2.class})
  static class Config {

  }

  @EnableAutoConfiguration(exclude = JacksonAutoConfiguration.class)
  static class Config1 {

  }

  @EnableAutoConfiguration(exclude = TaskExecutionAutoConfiguration.class)
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
