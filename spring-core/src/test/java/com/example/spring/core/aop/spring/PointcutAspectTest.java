package com.example.spring.core.aop.spring;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.clearInvocations;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.example.spring.core.aop.spring.PointcutAspectTest.Config.CommonPointcutAspect;
import com.example.spring.core.aop.spring.annotation.Auditable;
import com.example.spring.core.aop.spring.annotation.Auditable.LogMode;
import com.example.spring.core.aop.spring.service.AnotherService;
import com.example.spring.core.aop.spring.service.SomeService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.aop.AopAutoConfiguration;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Component;

@SpringBootTest
@Slf4j
// For AOP Use @AopAutoConfiguration or @org.springframework.context.annotation.EnableAspectJAutoProxy
// @EnableAspectJAutoProxy
class PointcutAspectTest {

  @Configuration
  @Import({AopAutoConfiguration.class, JacksonAutoConfiguration.class, ServicesConfig.class,
      CommonPointcuts.class})
  static class Config {

    @Component
    @Aspect
    public static class CommonPointcutAspect {

      @Autowired
      private LoggingAspectService service;
      @Autowired
      ObjectMapper objectMapper;

      @Around("CommonPointcuts.logElapsedTimeAnnotation()")
      public Object handle(ProceedingJoinPoint joinPoint) throws Throwable {
        return service.logProfiling(joinPoint, this);
      }

      @AfterReturning(
          pointcut = "CommonPointcuts.logInvocationAnnotation()",
          returning = "retVal")
      public void afterReturning(Object retVal) {
        log.info("afterReturning :: {}", retVal);
      }

      @AfterThrowing(
          pointcut = "CommonPointcuts.targetSomeService()",
          throwing = "throwable")
      public void afterThrowing(Throwable throwable) {
        log.info("afterThrowing :: {}", throwable.getMessage());
      }

      @Around("CommonPointcuts.auditable(auditable)")
      public Object capturingAnnotationAnnotation(ProceedingJoinPoint joinPoint,
          Auditable auditable)
          throws Throwable {
        return switch (auditable.mode()) {
          case INVOCATION -> service.logInvocation(joinPoint, this);
          case ELAPSED_TIME -> service.logProfiling(joinPoint, this);
        };
      }
    }
  }

  @Autowired
  private SomeService someService;
  @Autowired
  private AnotherService anotherService;
  @SpyBean
  private CommonPointcutAspect commonPointcutAspect;

  @Test
  void test() throws Throwable {
    clearMockInvocations();
    someService.hello();
    verify(commonPointcutAspect, times(1))
        .handle(any());

    clearMockInvocations();
    someService.processData("1");
    verify(commonPointcutAspect, times(1))
        .afterReturning(any());

    clearMockInvocations();
    assertThatThrownBy(() -> someService.throwError())
        .isInstanceOf(UnsupportedOperationException.class);
    verify(commonPointcutAspect, times(1))
        .afterThrowing(any());

    clearMockInvocations();
    someService.processDataAuditable("42");
    verify(commonPointcutAspect, times(1))
        .capturingAnnotationAnnotation(any(),
            argThat(auditable -> Objects.equals(auditable.mode(), LogMode.INVOCATION)));
  }

  private void clearMockInvocations() {
    clearInvocations(
        commonPointcutAspect);
  }

}
