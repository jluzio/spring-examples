package com.example.spring.core.aop.spring;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.clearInvocations;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.example.spring.core.aop.spring.ExecutionAspectTest.Config.AroundExecutionLogAspect;
import com.example.spring.core.aop.spring.service.AnotherService;
import com.example.spring.core.aop.spring.service.SomeService;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.aop.AopAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Component;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

@SpringBootTest
@Slf4j
class ExecutionAspectTest {

  @Configuration
  @Import({AopAutoConfiguration.class, ServicesConfig.class})
  static class Config {

    @Component
    @Aspect
    public static class AroundExecutionLogAspect {

      @Autowired
      private LoggingAspectService service;

      @Around("execution(* hello(..))")
      public Object handleMethod(ProceedingJoinPoint joinPoint) throws Throwable {
        return service.logProfiling(joinPoint, this);
      }

      @Around("execution(* com.example.spring.core.aop.spring.service..*.processData(..))")
      public Object handleMethodInPackage(ProceedingJoinPoint joinPoint) throws Throwable {
        return service.logProfiling(joinPoint, this);
      }
    }
  }

  @Autowired
  private SomeService someService;
  @Autowired
  private AnotherService anotherService;
  @MockitoSpyBean
  private AroundExecutionLogAspect aroundExecutionLogAspect;

  @Test
  void test() throws Throwable {
    clearMockInvocations();
    someService.hello();
    verify(aroundExecutionLogAspect, times(1))
        .handleMethod(any());
    verify(aroundExecutionLogAspect, times(0))
        .handleMethodInPackage(any());

    clearMockInvocations();
    someService.processData("1");
    verify(aroundExecutionLogAspect, times(0))
        .handleMethod(any());
    verify(aroundExecutionLogAspect, times(1))
        .handleMethodInPackage(any());

    clearMockInvocations();
    assertThatThrownBy(() -> someService.throwError())
        .isInstanceOf(UnsupportedOperationException.class);
    verify(aroundExecutionLogAspect, times(0))
        .handleMethod(any());
    verify(aroundExecutionLogAspect, times(0))
        .handleMethodInPackage(any());

    clearMockInvocations();
    anotherService.hello();
    verify(aroundExecutionLogAspect, times(1))
        .handleMethod(any());
    verify(aroundExecutionLogAspect, times(0))
        .handleMethodInPackage(any());
  }

  private void clearMockInvocations() {
    clearInvocations(
        aroundExecutionLogAspect);
  }

}
