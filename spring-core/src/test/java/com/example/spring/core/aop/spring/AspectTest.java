package com.example.spring.core.aop.spring;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.clearInvocations;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.example.spring.core.aop.spring.service.AnotherService;
import com.example.spring.core.aop.spring.service.SomeService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootTest
@Slf4j
@ComponentScan(basePackageClasses = AspectTest.class)
@EnableAspectJAutoProxy
class AspectTest {

  @Autowired
  private SomeService someService;
  @Autowired
  private AnotherService anotherService;
  @SpyBean
  private AroundAnnotationLogAspect aroundAnnotationLogAspect;
  @SpyBean
  private AroundBeanLogAspect aroundBeanLogAspect;
  @SpyBean
  private AroundExecutionLogAspect aroundExecutionLogAspect;
  @SpyBean
  private AroundWithinLogAspect aroundWithinLogAspect;

  @Test
  void test() throws Throwable {
    clearMockInvocations();
    someService.hello();
    verify(aroundAnnotationLogAspect, times(1))
        .handleMethod(any());
    verify(aroundAnnotationLogAspect, times(0))
        .handleType(any());
    verify(aroundBeanLogAspect, times(1))
        .handle(any());
    verify(aroundExecutionLogAspect, times(1))
        .handleMethod(any());
    verify(aroundExecutionLogAspect, times(0))
        .handleMethodInPackage(any());
    verify(aroundWithinLogAspect, times(1))
        .handle(any());

    clearMockInvocations();
    someService.processData("1");
    verify(aroundAnnotationLogAspect, times(0))
        .handleMethod(any());
    verify(aroundAnnotationLogAspect, times(0))
        .handleType(any());
    verify(aroundBeanLogAspect, times(1))
        .handle(any());
    verify(aroundExecutionLogAspect, times(0))
        .handleMethod(any());
    verify(aroundExecutionLogAspect, times(1))
        .handleMethodInPackage(any());
    verify(aroundWithinLogAspect, times(1))
        .handle(any());

    clearMockInvocations();
    assertThatThrownBy(() -> someService.throwError())
        .isInstanceOf(UnsupportedOperationException.class);
    verify(aroundAnnotationLogAspect, times(0))
        .handleMethod(any());
    verify(aroundAnnotationLogAspect, times(0))
        .handleType(any());
    verify(aroundBeanLogAspect, times(1))
        .handle(any());
    verify(aroundExecutionLogAspect, times(0))
        .handleMethod(any());
    verify(aroundExecutionLogAspect, times(0))
        .handleMethodInPackage(any());
    verify(aroundWithinLogAspect, times(1))
        .handle(any());

    clearMockInvocations();
    anotherService.hello();
    verify(aroundAnnotationLogAspect, times(0))
        .handleMethod(any());
    verify(aroundAnnotationLogAspect, times(1))
        .handleType(any());
    verify(aroundAnnotationLogAspect, times(1))
        .handleType(any());
    verify(aroundBeanLogAspect, times(0))
        .handle(any());
    verify(aroundExecutionLogAspect, times(1))
        .handleMethod(any());
    verify(aroundExecutionLogAspect, times(0))
        .handleMethodInPackage(any());
    verify(aroundWithinLogAspect, times(1))
        .handle(any());
  }

  private void clearMockInvocations() {
    clearInvocations(
        aroundAnnotationLogAspect, aroundBeanLogAspect, aroundExecutionLogAspect,
        aroundWithinLogAspect);
  }

}
