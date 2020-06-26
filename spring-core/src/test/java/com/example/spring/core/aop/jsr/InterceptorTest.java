package com.example.spring.core.aop.jsr;

import com.example.spring.core.aop.jsr.InterceptorTest.Config.SomeBean;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.stereotype.Component;

@SpringBootTest
@Slf4j
public class InterceptorTest {

  @Autowired
  SomeBean someBean;

  @Test
  void test() throws InterruptedException {
    // TODO: make JSR Interceptors work
    someBean.someFunc();
  }

  //  @TestConfiguration
  @Configuration
  @ComponentScan("com.example.spring.core.aop.jsr")
  @EnableAspectJAutoProxy
  static class Config {

//    @Bean
//    LogExecutionTimeInterceptor interceptor() {
//      return new LogExecutionTimeInterceptor();
//    }

    @Component
    static class SomeBean {

      @LogExecutionTime
      public void someFunc() throws InterruptedException {
        Thread.sleep(1000l);
      }
    }
  }

}
