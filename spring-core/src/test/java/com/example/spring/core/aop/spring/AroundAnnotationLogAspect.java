package com.example.spring.core.aop.spring;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Aspect
public class AroundAnnotationLogAspect {

  @Autowired
  private LoggingAspect loggingAspect;

  @Around("@annotation(LogInvocation)")
//  @Around("@annotation(com.example.spring.core.aop.spring.LogInvocation)")
  public Object handleMethod(ProceedingJoinPoint joinPoint) throws Throwable {
    return loggingAspect.logInvocation(joinPoint, this);
  }

  /**
   * Note: method must be declared in the class, not in the parent class
   */
  @Around("@within(LogInvocation)")
  public Object handleType(ProceedingJoinPoint joinPoint) throws Throwable {
    return loggingAspect.logInvocation(joinPoint, this);
  }
}
