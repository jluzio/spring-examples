package com.example.spring.core.beans;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

@SpringBootTest
class ApplicationContextBeanTest {

  @Configuration
  static class Config {
    AtomicInteger counter = new AtomicInteger(0);

    @Bean
    Integer singletonValue() {
      return counter.incrementAndGet();
    }

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    Integer prototypeValue() {
      return counter.incrementAndGet();
    }

  }

  @Autowired
  ApplicationContext context;

  @Test
  void test() {
    assertThat(context.getBean("singletonValue"))
        .isEqualTo(1);
    assertThat(context.getBean("singletonValue"))
        .isEqualTo(1);

    assertThat(context.getBean("prototypeValue"))
        .isEqualTo(2);
    assertThat(context.getBean("prototypeValue"))
        .isEqualTo(3);
  }
}
