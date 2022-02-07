package com.example.spring.core.validator.jsr380;

import static org.assertj.core.api.Assertions.assertThat;

import javax.validation.Validator;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Slf4j
class ValidatorTest {

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
        .age(123)
        .build();

    var validConstraintViolations = validator.validate(validPerson);
    assertThat(validConstraintViolations).isEmpty();

    var invalidConstraintViolations = validator.validate(invalidPerson);
    log.info("constraintViolations: {}", invalidConstraintViolations);
    assertThat(invalidConstraintViolations).isNotEmpty();
  }
}
