package com.example.spring.core.validator.jsr380;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.context.MessageSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.validation.ValidationAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@SpringBootTest
@Slf4j
class ValidatorTest {

  @Configuration
  @Import({ValidationAutoConfiguration.class, MessageSourceAutoConfiguration.class})
  static class Config {

  }

  @Autowired
  Validator validator;

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
        .age(6)
        .build();

    var validConstraintViolations = validator.validate(validPerson);
    assertThat(validConstraintViolations).isEmpty();

    var invalidConstraintViolations = validator.validate(invalidPerson);
    log.info("constraintViolations: {}", invalidConstraintViolations);
    assertThat(invalidConstraintViolations).isNotEmpty();
    assertThat(invalidConstraintViolations)
        .map(ConstraintViolation::getMessage)
        .contains("Must be able to vote!");
  }
}
