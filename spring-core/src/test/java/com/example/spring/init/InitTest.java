package com.example.spring.init;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
public class InitTest {

  @Test
  void test() {
    ApplicationContext context = new AnnotationConfigApplicationContext(Config.class);
    log.info((String) context.getBean("someBean"));
  }

  @Configuration
  static class Config {

    @Bean
    String someBean() {
      return "someBean";
    }
  }

}
