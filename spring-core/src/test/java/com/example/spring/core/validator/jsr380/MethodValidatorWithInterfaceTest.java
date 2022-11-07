package com.example.spring.core.validator.jsr380;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import javax.validation.ConstraintViolationException;
import javax.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;

@SpringBootTest
@Slf4j
class MethodValidatorWithInterfaceTest {

  interface ConsumerService {

    void consume(@Valid Person person);
  }

  @Configuration
  static class TestConfig {

    @Bean
    MethodValidationPostProcessor methodValidationPostProcessor() {
      return new MethodValidationPostProcessor();
    }

    @Service
    @Validated
    static class ConsumerServiceImpl implements ConsumerService {

      public void consume(@Valid Person person) {
        log.info("valid person: {}", person);
      }
    }
  }

  @Autowired
  ConsumerService service;

  @Test
  void test() {
    var validPerson = Person.builder()
        .id("id")
        .name("required-name")
        .age(42)
        .build();
    var invalidPerson = Person.builder()
        .id("id")
        .name("")
        .age(123)
        .build();

    service.consume(validPerson);

    assertThatThrownBy(() -> service.consume(invalidPerson))
        .isInstanceOf(ConstraintViolationException.class);
  }
}
