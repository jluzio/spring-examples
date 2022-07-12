package com.example.spring.batch.playground.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@ConditionalOnProperty(ConditionalOnAutoConfigureApp.PROPERTY)
public @interface ConditionalOnAutoConfigureApp {

  String PROPERTY = "app.auto-configure";

}
