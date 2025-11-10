package com.example.spring.framework.beans;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Slf4j
@SpringBootTest(classes = LazyBeanTest.Config.class)
class LazyBeanTest {

  @Import({ProducerConfig.class, ProducerBean.class, ConsumerBean.class})
  static class Config {

    public Config() {
      log.info("{}<init>", getClass().getSimpleName());
    }
  }

  @Lazy
  @Configuration
//  @Component
  static class ProducerConfig {

    public ProducerConfig() {
      log.info("{}<init>", getClass().getSimpleName());
    }

    @Bean
    FooBean fooBean(ProducerBean producerBean) {
      return producerBean.fooBean();
    }
  }

  @Component
  static class ProducerBean {

    public ProducerBean() {
      log.info("{}<init>", getClass().getSimpleName());
    }

    FooBean fooBean() {
      log.info("{}<fooBean>", getClass().getSimpleName());
      return new FooBean();
    }
  }

  @Component
  static class ConsumerBean {

    @Autowired
    @Lazy
    private FooBean fooBean;

    public ConsumerBean() {
      log.info("{}<init>", getClass().getSimpleName());
    }

    void fooBar() {
      fooBean.bar();
    }
  }

  static class FooBean {

    public FooBean() {
      log.info("{}<init>", getClass().getSimpleName());
    }

    public void bar() {
      log.info("{}<bar>", getClass().getSimpleName());
    }
  }

  @Autowired
  ConsumerBean consumerBean;

  @Test
  void test() {
    log.info("Test start");
    consumerBean.fooBar();
  }

}
