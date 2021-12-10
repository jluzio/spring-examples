package com.example.spring.core.aop.spring;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Aspect
public class AroundExecutionLogAspect {

  @Autowired
  private LoggingAspect loggingAspect;

  @Around("execution(* hello(..))")
  public Object handleMethod(ProceedingJoinPoint joinPoint) throws Throwable {
    return loggingAspect.logInvocation(joinPoint, this);
  }

  @Around("execution(* com.example.spring.core.aop.spring.service..*.processData(..))")
  public Object handleMethodInPackage(ProceedingJoinPoint joinPoint) throws Throwable {
    return loggingAspect.logInvocation(joinPoint, this);
  }

}
