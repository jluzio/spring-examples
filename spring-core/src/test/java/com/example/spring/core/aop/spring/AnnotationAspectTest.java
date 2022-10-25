package com.example.spring.core.aop.spring;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.clearInvocations;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.example.spring.core.aop.spring.AnnotationAspectTest.AroundAnnotationLogAspect;
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
@Import({ServicesConfig.class, AroundAnnotationLogAspect.class})
class AnnotationAspectTest {

  @Component
  @Aspect
  public static class AroundAnnotationLogAspect {

    @Autowired
    private LoggingAspectService service;

    // Note: simplified format only seems to works if annotation is in the same package of aspect
    // @Around("@annotation(LogElapsedTime)")
    @Around("@annotation(com.example.spring.core.aop.spring.annotation.LogElapsedTime)")
    public Object handleMethod(ProceedingJoinPoint joinPoint) throws Throwable {
      return service.logTimeElapsed(joinPoint, this);
    }

    /**
     * Note: method must be declared in the class, not in the parent class
     */
    @Around("@within(com.example.spring.core.aop.spring.annotation.LogElapsedTime)")
    public Object handleType(ProceedingJoinPoint joinPoint) throws Throwable {
      return service.logTimeElapsed(joinPoint, this);
    }
  }


  @Autowired
  private SomeService someService;
  @Autowired
  private AnotherService anotherService;
  @SpyBean
  private AroundAnnotationLogAspect aroundAnnotationLogAspect;

  @Test
  void test() throws Throwable {
    clearMockInvocations();
    someService.hello();
    verify(aroundAnnotationLogAspect, times(1))
        .handleMethod(any());
    verify(aroundAnnotationLogAspect, times(0))
        .handleType(any());

    clearMockInvocations();
    someService.processData("1");
    verify(aroundAnnotationLogAspect, times(0))
        .handleMethod(any());
    verify(aroundAnnotationLogAspect, times(0))
        .handleType(any());

    clearMockInvocations();
    assertThatThrownBy(() -> someService.throwError())
        .isInstanceOf(UnsupportedOperationException.class);
    verify(aroundAnnotationLogAspect, times(0))
        .handleMethod(any());
    verify(aroundAnnotationLogAspect, times(0))
        .handleType(any());

    clearMockInvocations();
    anotherService.hello();
    verify(aroundAnnotationLogAspect, times(0))
        .handleMethod(any());
    verify(aroundAnnotationLogAspect, times(1))
        .handleType(any());
    verify(aroundAnnotationLogAspect, times(1))
        .handleType(any());
  }

  private void clearMockInvocations() {
    clearInvocations(
        aroundAnnotationLogAspect);
  }

}
