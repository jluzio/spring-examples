package com.example.spring.core.aop.spring;

import com.example.spring.core.aop.spring.annotation.Auditable;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

@Aspect
public class CommonPointcuts {

  @Pointcut("within(com.example.spring.core..*)")
  public void appCode() {
  }

  @Pointcut("appCode() && @annotation(com.example.spring.core.aop.spring.annotation.LogInvocation)")
  public void logInvocationAnnotation() {
  }

  @Pointcut("appCode() && @annotation(com.example.spring.core.aop.spring.annotation.LogElapsedTime)")
  public void logElapsedTimeAnnotation() {
  }

  @Pointcut("appCode() && target(com.example.spring.core.aop.spring.service.SomeService)")
  public void targetSomeService() {
  }

  @Pointcut("appCode() && @annotation(auditable)")
  public void auditable(Auditable auditable) {
  }

}
