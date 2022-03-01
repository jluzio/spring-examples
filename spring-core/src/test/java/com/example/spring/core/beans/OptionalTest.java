package com.example.spring.core.beans;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;
import lombok.Builder;
import lombok.Data;
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
  }

  @Data
  @Builder
  static class OptionalItem {

    private String id;
  }


  @Autowired
  Optional<OptionalItem> optionalItem1;
  @Autowired(required = false)
  OptionalItem optionalItem2;
  @Autowired
  @Nullable
  OptionalItem optionalItem3;
  @Autowired
  Optional<String> optionalString;

  @Test
  void test() {
    assertThat(optionalItem1).isEmpty();
    assertThat(optionalItem2).isNull();
    assertThat(optionalItem3).isNull();
    assertThat(optionalString).isPresent();
  }

}
