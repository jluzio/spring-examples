package com.example.spring.core.aop.jsr;

import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.Interceptor;
import jakarta.interceptor.InvocationContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Interceptor
@Slf4j
@Component
@LogExecutionTime
public class LogExecutionTimeInterceptor {

  @AroundInvoke
  public Object aroundInvoke(InvocationContext ctx) throws Exception {
    long start = System.currentTimeMillis();
    Object proceed = ctx.proceed();
    long executionTime = System.currentTimeMillis() - start;
    log.info("Interceptor :: {} executed in {}ms", ctx.getMethod().getName(), executionTime);
    return proceed;
  }

}
