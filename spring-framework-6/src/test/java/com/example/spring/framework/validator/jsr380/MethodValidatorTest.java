package com.example.spring.framework.validator.jsr380;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.validation.annotation.Validated;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;

@SpringBootTest
@Slf4j
class MethodValidatorTest {

  static final Person VALID_PERSON = Person.builder()
      .id("id")
      .name("required-name")
      .age(42)
      .build();
  static final Person INVALID_PERSON = Person.builder()
      .id("id")
      .name("")
      .age(123)
      .build();

  @Configuration
  @Import({SomeService.class})
  static class Config {

    @Bean
    MethodValidationPostProcessor methodValidationPostProcessor() {
      return new MethodValidationPostProcessor();
    }
  }

  @Validated
  static class FactoryConfig {

    @Bean
    @Valid
    public Person invalidPersonByConfig() {
      return INVALID_PERSON;
    }
  }

  @Validated
  static class SomeService {

    public void consume(@Valid Person person) {
      log.info("valid person: {}", person);
    }

    @Valid
    public Person invalidPersonSupplier() {
      return INVALID_PERSON;
    }

    @Valid
    public Person validPersonSupplier() {
      return VALID_PERSON;
    }
  }

  @Autowired
  SomeService service;

  @Test
  void test_consumer_and_supplier() {
    service.consume(VALID_PERSON);

    assertThatThrownBy(() -> service.consume(INVALID_PERSON))
        .isInstanceOf(ConstraintViolationException.class);

    assertThat(service.validPersonSupplier())
        .isEqualTo(VALID_PERSON);

    assertThatThrownBy(() -> service.invalidPersonSupplier())
        .isInstanceOf(ConstraintViolationException.class);
  }

  @Test
  void test_config_supplier() {
    var app = new ApplicationContextRunner()
        // alternative to creating MethodValidationPostProcessor
        //.withConfiguration(AutoConfigurations.of(ValidationAutoConfiguration.class))
        .withUserConfiguration(Config.class, FactoryConfig.class);

    app.run(ctx -> {
      log.info("context: {}", ctx);
      assertThatThrownBy(() -> ctx.getBean("invalidPersonSupplier"))
          .satisfies((Throwable throwable) -> log.info("Throwable", throwable))
          // only due to ApplicationContextRunner
          .isInstanceOf(IllegalStateException.class)
          // expected cause
          .hasCauseInstanceOf(BeanCreationException.class)
      ;
    });
  }
}
