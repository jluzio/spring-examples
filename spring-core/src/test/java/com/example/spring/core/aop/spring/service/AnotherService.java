package com.example.spring.core.aop.spring.service;

import com.example.spring.core.aop.spring.LogInvocation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@LogInvocation
@Slf4j
public class AnotherService implements GreetingService {

  @Override
  public void hello() {
    log.info("Hello AOP!");
  }

}
