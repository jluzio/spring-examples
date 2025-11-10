package com.example.spring.framework.validator.spring;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.validation.DataBinder;

@SpringBootTest
@Slf4j
class SpringValidatorTest {

  @Test
  void test_data_binder() {
    Person person = Person.builder()
        .id("id")
        .age(123)
        .name("")
        .build();

    DataBinder dataBinder = new DataBinder(person);

    dataBinder.addValidators(new PersonValidator());

    dataBinder.validate(person);

    dataBinder.getBindingResult().getAllErrors().stream()
        .map(Object::toString)
        .forEach(log::info);
  }
}
