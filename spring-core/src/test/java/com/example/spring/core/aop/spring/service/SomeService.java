package com.example.spring.core.aop.spring.service;

import com.example.spring.core.aop.spring.LogInvocation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class SomeService {

  @LogInvocation
  public void hello() {
    log.info("Hello AOP!");
  }

  public String processData(String id) {
    log.info("Process data with id: {}", id);
    return "processed-data-%s".formatted(id);
  }

  public String throwError() {
    log.info("Throw error");
    throw new UnsupportedOperationException("Can't process");
  }

}
