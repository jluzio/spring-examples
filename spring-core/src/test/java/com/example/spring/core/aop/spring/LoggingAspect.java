package com.example.spring.core.aop.spring;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class LoggingAspect {

  public Object logExecutionTime(ProceedingJoinPoint joinPoint, Object caller) throws Throwable {
    long start = System.currentTimeMillis();
    Object proceed = joinPoint.proceed();
    long executionTime = System.currentTimeMillis() - start;
    log.info("{} :: {} executed in {}ms", caller.getClass().getSimpleName(), joinPoint.getSignature(), executionTime);
    return proceed;
  }
}
