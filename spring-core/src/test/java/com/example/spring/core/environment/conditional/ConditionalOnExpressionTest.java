package com.example.spring.core.environment.conditional;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@SpringBootTest(properties = "app.somevar=bean1")
class ConditionalOnExpressionTest {

  @Configuration
  static class Config {

    @Bean
    @ConditionalOnExpression("'${app.somevar}' == 'bean1'")
    public String bean1() {
      return "bean1";
    }

    @Bean
    @ConditionalOnExpression("'${app.somevar}' == 'bean2'")
    public String bean2() {
      return "bean2";
    }
  }

  @Autowired(required = false)
  @Qualifier("bean1")
  Optional<String> bean1;
  @Autowired(required = false)
  @Qualifier("bean2")
  Optional<String> bean2;

  @Test
  void test_bean() {
    assertThat(bean1).isPresent().hasValue("bean1");
    assertThat(bean2).isEmpty();
  }
}
