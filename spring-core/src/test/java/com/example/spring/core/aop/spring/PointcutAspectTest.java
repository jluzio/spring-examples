package com.example.spring.core.aop.spring;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.clearInvocations;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.example.spring.core.aop.spring.PointcutAspectTest.CommonPointcutAspect;
import com.example.spring.core.aop.spring.service.AnotherService;
import com.example.spring.core.aop.spring.service.SomeService;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
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
@Import({ServicesConfig.class, CommonPointcuts.class, CommonPointcutAspect.class})
class PointcutAspectTest {

  @Component
  @Aspect
  public static class CommonPointcutAspect {

    @Autowired
    private LoggingAspectService service;

    @Around("com.example.spring.core.aop.spring.CommonPointcuts.logElapsedTimeAnnotation()")
    public Object handle(ProceedingJoinPoint joinPoint) throws Throwable {
      return service.logTimeElapsed(joinPoint, this);
    }

    @AfterReturning(
        pointcut = "com.example.spring.core.aop.spring.CommonPointcuts.logInvocationAnnotation()",
        returning = "retVal")
    public void afterReturning(Object retVal) {
      log.info("afterReturning :: {}", retVal);
    }

    @AfterThrowing(
        pointcut = "com.example.spring.core.aop.spring.CommonPointcuts.beanSomeService()",
        throwing = "throwable")
    public void afterThrowing(Throwable throwable) {
      log.info("afterThrowing :: {}", throwable.getMessage());
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
  }

  private void clearMockInvocations() {
    clearInvocations(
        commonPointcutAspect);
  }

}
