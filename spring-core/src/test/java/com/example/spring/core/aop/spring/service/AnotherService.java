package com.example.spring.core.aop.spring.service;

import com.example.spring.core.aop.spring.LogInvocation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class AnotherService {

  @LogInvocation
  public void hello() {
    log.info("Hello AOP!");
  }
}
