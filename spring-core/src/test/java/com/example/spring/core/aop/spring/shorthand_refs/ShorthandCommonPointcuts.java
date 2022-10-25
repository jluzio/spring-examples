package com.example.spring.core.aop.spring.shorthand_refs;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

@Aspect
public class ShorthandCommonPointcuts {

  // Omitted package, since it's visible in the same package.
  @Pointcut("@annotation(LogStuff)")
  public void logStuffAnnotation() {
  }

}
