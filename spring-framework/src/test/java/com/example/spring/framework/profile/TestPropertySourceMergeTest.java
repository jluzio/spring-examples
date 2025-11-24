package com.example.spring.framework.profile;

import static org.assertj.core.api.Assertions.assertThat;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.test.context.TestPropertySource;
import tools.jackson.databind.ObjectMapper;

@SpringBootTest(classes = TestPropertySourceMergeTest.Config.class, properties = "debug=true")
@Slf4j
@TestPropertySource(properties = {
    "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration",
})
@TestPropertySource(properties = {
    "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.task.TaskExecutionAutoConfiguration",
})
class TestPropertySourceMergeTest {

  @Configuration
  @EnableAutoConfiguration
  static class Config {

  }

  @Autowired(required = false)
  ObjectMapper objectMapper;
  @Autowired(required = false)
  TaskExecutor taskExecutor;
  @Value("${spring.autoconfigure.exclude:}")
  String springAutoconfigureExclude;

  @Test
  void test() {
    log.debug(springAutoconfigureExclude);
    assertThat(springAutoconfigureExclude)
        .isNotEmpty();

    assertThat(objectMapper)
        .isNotNull();
    assertThat(taskExecutor)
        .isNull();
  }

}
