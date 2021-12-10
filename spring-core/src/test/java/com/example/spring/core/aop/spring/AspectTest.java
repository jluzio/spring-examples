package com.example.spring.core.aop.spring;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.example.spring.core.aop.spring.AspectTest.Config.SomeBean;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.stereotype.Component;

@SpringBootTest
@Slf4j
class AspectTest {

  @Configuration
  @ComponentScan(basePackageClasses = AspectTest.class)
  @EnableAspectJAutoProxy
  static class Config {

    @Component
    @Qualifier("someBean")
    public static class SomeBean {

      @LogInvocation
      public void hello() {
        log.info("Hello AOP!");
      }

      @LogInvocation
      public String processData(String id) {
        log.info("Process data with id: {}!", id);
        return "processed-data-%s".formatted(id);
      }

      @LogInvocation
      public String throwError() {
        log.info("Throw error!");
        throw new UnsupportedOperationException("Can't process");
      }
    }
  }

  @Autowired
  private SomeBean someBean;
  @SpyBean
  private AroundAnnotationLogAspect aroundAnnotationLogAspect;
  @SpyBean
  private AroundBeanLogAspect aroundBeanLogAspect;
  @SpyBean
  private AroundExecutionLogAspect aroundExecutionLogAspect;

  @Test
  void test() throws Throwable {
    someBean.hello();
    someBean.processData("1");
    assertThatThrownBy(() -> someBean.throwError())
        .isInstanceOf(UnsupportedOperationException.class);

    verify(aroundAnnotationLogAspect, times(3))
        .handle(any());
    verify(aroundBeanLogAspect, times(3))
        .handle(any());
    verify(aroundExecutionLogAspect, times(1))
        .handle(any());
  }

}
