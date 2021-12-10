package com.example.spring.core.aop.spring;

import com.example.spring.core.aop.spring.AspectTest.Config.SomeBean;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.stereotype.Component;

@SpringBootTest
@Slf4j
class AspectTest {

  @Autowired
  SomeBean someBean;

  @Test
  void test() throws Exception {
    log.info("someBean: {}", someBean);
    someBean.someMethod();
  }

  //  @TestConfiguration
  @Configuration
  @ComponentScan("com.example.spring.core.aop.spring")
  @EnableAspectJAutoProxy
  static class Config {

    @Component
    @Qualifier("someBean")
    public static class SomeBean {

      @LogExecutionTime
      public void someMethod() throws InterruptedException {
        Thread.sleep(1000l);
      }
    }
  }

}
