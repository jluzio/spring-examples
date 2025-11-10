package com.example.spring.framework.beans;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@SpringBootTest
@Slf4j
class BeanTest {

  @Autowired
  @Qualifier("beanId1")
  String bean1;

  @Autowired
  @Qualifier("beanId2")
  String bean2;

  @Autowired
  @Qualifier("beanAlias1")
  String beanAlias1;

  @Autowired
  @Qualifier("subsystem-beanAlias1")
  String subsystemBeanAlias1;

  @Test
  void test() {
    log.info("bean1: {}", bean1);
    log.info("bean2: {}", bean2);
    log.info("beanAlias1: {}", beanAlias1);
    log.info("subsystemBeanAlias1: {}", subsystemBeanAlias1);
  }

  @Configuration
  static class Config {

    @Bean
    String beanId1() {
      return "bean1";
    }

    @Bean("beanId2")
    String beanId2() {
      return "bean2";
    }

    @Bean({"beanAlias1", "subsystem-beanAlias1"})
    String beanAlias1() {
      return "beanAlias1";
    }
  }

}
