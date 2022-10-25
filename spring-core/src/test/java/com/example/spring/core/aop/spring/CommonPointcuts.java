package com.example.spring.core.aop.spring;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

@Aspect
public class CommonPointcuts {

  @Pointcut("@annotation(com.example.spring.core.aop.spring.annotation.LogInvocation)")
  public void logInvocationAnnotation() {
  }

  @Pointcut("@annotation(com.example.spring.core.aop.spring.annotation.LogElapsedTime)")
  public void logElapsedTimeAnnotation() {
  }

  @Pointcut("bean(someService)")
  public void beanSomeService() {
  }

}
