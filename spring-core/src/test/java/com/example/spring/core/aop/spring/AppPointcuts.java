package com.example.spring.core.aop.spring;

import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

/**
 * TODO: how to use these pointcuts?
 */
@Aspect
public class AppPointcuts {

  @Pointcut("bean(someBean)")
  public void someBean() {
  }

  @Around("execution(* someMethod(..))")
  public void someMethod() {
  }

}
