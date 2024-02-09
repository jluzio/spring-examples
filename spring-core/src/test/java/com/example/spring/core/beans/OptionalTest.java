package com.example.spring.core.beans;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.Nullable;

@SpringBootTest
@Slf4j
class OptionalTest {

  @Configuration
  static class Config {

    @Bean
    String testString() {
      return "testString";
    }

    @Bean
    Integer beanCreatedWithOptionals(
        Optional<NonExisting> optionalItem,
        Optional<String> optionalString) {
      return 42;
    }
  }

  @Autowired
  Optional<NonExisting> optionalEmpty1;
  @Autowired(required = false)
  NonExisting optionalEmpty2;
  @Autowired
  @Nullable
  NonExisting optionalEmpty3;
  @Autowired
  Optional<String> optionalPresentString;

  @Test
  void test() {
    assertThat(optionalEmpty1).isEmpty();
    assertThat(optionalEmpty2).isNull();
    assertThat(optionalEmpty3).isNull();
    assertThat(optionalPresentString).isPresent();
  }

  record NonExisting(String id) {

  }

}
