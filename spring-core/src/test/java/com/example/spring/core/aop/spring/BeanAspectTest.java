package com.example.spring.core.aop.spring;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.clearInvocations;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.example.spring.core.aop.spring.BeanAspectTest.AroundBeanLogAspect;
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
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.stereotype.Component;

@SpringBootTest(classes = {
    AopAutoConfiguration.class, ServicesConfig.class, AroundBeanLogAspect.class})
@Slf4j
class BeanAspectTest {

  @Component
  @Aspect
  public static class AroundBeanLogAspect {

    @Autowired
    private LoggingAspectService service;

    // using default component name
    @Around("bean(com.example.spring.core.aop.spring.service.SomeService)")
    public Object handle(ProceedingJoinPoint joinPoint) throws Throwable {
      return service.logTimeElapsed(joinPoint, this);
    }
  }

  @Autowired
  private SomeService someService;
  @Autowired
  private AnotherService anotherService;
  @SpyBean
  private AroundBeanLogAspect aroundBeanLogAspect;

  @Test
  void test() throws Throwable {
    clearMockInvocations();
    someService.hello();
    verify(aroundBeanLogAspect, times(1))
        .handle(any());

    clearMockInvocations();
    someService.processData("1");
    verify(aroundBeanLogAspect, times(1))
        .handle(any());

    clearMockInvocations();
    assertThatThrownBy(() -> someService.throwError())
        .isInstanceOf(UnsupportedOperationException.class);
    verify(aroundBeanLogAspect, times(1))
        .handle(any());

    clearMockInvocations();
    anotherService.hello();
    verify(aroundBeanLogAspect, times(0))
        .handle(any());
  }

  private void clearMockInvocations() {
    clearInvocations(
        aroundBeanLogAspect);
  }

}
