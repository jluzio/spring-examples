package com.example.spring.framework.validator.jsr380;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import jakarta.validation.ConstraintViolationException;
import jakarta.validation.constraints.Min;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;

class MethodValidationPostProcessorTest {

  private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
//      .withConfiguration(AutoConfigurations.of(ValidationAutoConfiguration.class))
      .withUserConfiguration(ValidationConfig.class); // Load validation config

  @Test
  void testMethodValidation() {
    contextRunner.run(context -> {
      MyService myService = context.getBean(MyService.class);

      // Valid input (should not throw an exception)
      myService.process(10);

      // Invalid input (should throw ConstraintViolationException)
      assertThatThrownBy(() -> myService.process(-5))
          .isInstanceOf(ConstraintViolationException.class)
          .hasMessageContaining("must be greater than or equal to 0");
    });
  }

  @Configuration
  static class ValidationConfig {

    @Bean
    public MethodValidationPostProcessor methodValidationPostProcessor() {
      return new MethodValidationPostProcessor();
    }

    @Bean
    public MyService myService() {
      return new MyService();
    }
  }

  @Validated
  static class MyService {
    public void process(@Min(0) int value) {
      System.out.println("Processing value: " + value);
    }
  }
}
