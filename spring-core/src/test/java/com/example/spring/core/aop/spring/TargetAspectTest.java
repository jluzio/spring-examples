package com.example.spring.core.aop.spring;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.clearInvocations;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.example.spring.core.aop.spring.TargetAspectTest.AroundTargetLogAspect;
import com.example.spring.core.aop.spring.service.AnotherService;
import com.example.spring.core.aop.spring.service.SomeService;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Component;

@SpringBootTest
@Slf4j
@EnableAspectJAutoProxy
@Import({ServicesConfig.class, AroundTargetLogAspect.class})
class TargetAspectTest {
  @Component
  @Aspect
  public static class AroundTargetLogAspect {

    @Autowired
    private LoggingAspectService service;

    @Around("target(com.example.spring.core.aop.spring.service.GreetingService)")
    public Object handle(ProceedingJoinPoint joinPoint) throws Throwable {
      return service.logTimeElapsed(joinPoint, this);
    }

    @Around("target(com.example.spring.core.aop.spring.service.GreetingService) && execution(* hello(..))")
    public Object handleHello(ProceedingJoinPoint joinPoint) throws Throwable {
      return service.logTimeElapsed(joinPoint, this);
    }

  }

  @Autowired
  private SomeService someService;
  @Autowired
  private AnotherService anotherService;
  @SpyBean
  private AroundTargetLogAspect aroundTargetLogAspect;

  @Test
  void test() throws Throwable {
    clearMockInvocations();
    someService.hello();
    verify(aroundTargetLogAspect, times(1))
        .handle(any());
    verify(aroundTargetLogAspect, times(1))
        .handleHello(any());

    clearMockInvocations();
    someService.processData("1");
    verify(aroundTargetLogAspect, times(1))
        .handle(any());
    verify(aroundTargetLogAspect, times(0))
        .handleHello(any());

    clearMockInvocations();
    assertThatThrownBy(() -> someService.throwError())
        .isInstanceOf(UnsupportedOperationException.class);
    verify(aroundTargetLogAspect, times(1))
        .handle(any());
    verify(aroundTargetLogAspect, times(0))
        .handleHello(any());

    clearMockInvocations();
    anotherService.hello();
    verify(aroundTargetLogAspect, times(1))
        .handle(any());
    verify(aroundTargetLogAspect, times(1))
        .handleHello(any());
  }

  private void clearMockInvocations() {
    clearInvocations(
        aroundTargetLogAspect);
  }

}
