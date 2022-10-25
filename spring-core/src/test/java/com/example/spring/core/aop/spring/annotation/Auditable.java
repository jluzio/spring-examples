package com.example.spring.core.aop.spring.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Auditable {

  enum LogMode {
    ELAPSED_TIME, INVOCATION
  }

  LogMode mode() default LogMode.ELAPSED_TIME;

}
