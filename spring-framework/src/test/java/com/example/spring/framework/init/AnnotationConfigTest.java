package com.example.spring.framework.init;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
class AnnotationConfigTest {

  @Test
  void test() {
    AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(
        Config.class);
    context.registerBean("newBean", String.class, "newBeanValue");

    log.info((String) context.getBean("someBean"));
    log.info((String) context.getBean("newBean"));
  }

  @Configuration
  static class Config {

    @Bean
    String someBean() {
      return "someBean";
    }
  }

}
