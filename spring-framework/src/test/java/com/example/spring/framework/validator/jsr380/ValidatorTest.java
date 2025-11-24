package com.example.spring.framework.validator.jsr380;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.context.MessageSourceAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.validation.autoconfigure.ValidationAutoConfiguration;
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
        .hobbies(List.of("dancing"))
        .build();
    var invalidPerson1 = Person.builder()
        .id("id")
        .name("")
        .age(6)
        .hobbies(null)
        .build();
    var invalidPerson2 = Person.builder()
        .id("id")
        .name("")
        .age(6)
        .hobbies(List.of(""))
        .build();

    var validConstraintViolations = validator.validate(validPerson);
    assertThat(validConstraintViolations).isEmpty();

    var invalidConstraintViolations1 = validator.validate(invalidPerson1);
    log.info("constraintViolations: {}", invalidConstraintViolations1);
    assertThat(invalidConstraintViolations1)
        .isNotEmpty()
        .map(ConstraintViolation::getMessage)
        .contains("must be able to vote!")
        .contains("must not be empty")
        .contains("hobbies must not be null");

    var invalidConstraintViolations2 = validator.validate(invalidPerson2);
    log.info("constraintViolations: {}", invalidConstraintViolations2);
    assertThat(invalidConstraintViolations2)
        .isNotEmpty()
        .map(ConstraintViolation::getMessage)
        .contains("hobby must not be empty");
  }
}
