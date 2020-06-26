package com.example.spring.core.validator.jsr380;

import javax.validation.Validator;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Slf4j
public class Jsr380PersonValidatorTest {
  @Autowired
  Validator validator;

  @Test
  void test() {
    var person = Person.builder()
        .id("id")
        .name("")
        .age(123)
        .build();

    var result = validator.validate(person);
    log.info("result: {}", result);
  }
}
