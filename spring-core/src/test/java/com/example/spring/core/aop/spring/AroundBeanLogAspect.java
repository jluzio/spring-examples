package com.example.spring.core.aop.spring;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Aspect
public class AroundBeanLogAspect {

  @Autowired
  private LoggingAspect loggingAspect;

  @Around("bean(someBean)")
  public Object handle(ProceedingJoinPoint joinPoint) throws Throwable {
    return loggingAspect.logInvocation(joinPoint, this);
  }

}
