package com.example.spring.core.aop.spring.service;

import com.example.spring.core.aop.spring.annotation.Auditable;
import com.example.spring.core.aop.spring.annotation.Auditable.LogMode;
import com.example.spring.core.aop.spring.annotation.LogElapsedTime;
import com.example.spring.core.aop.spring.annotation.LogInvocation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
//@Component("someService")
@Slf4j
public class SomeService implements GreetingService {

  @Override
  @LogElapsedTime
  public void hello() {
    log.info("Hello AOP!");
  }

  @LogInvocation
  public String processData(String id) {
    log.info("Process data with id: {}", id);
    return "processed-data-%s".formatted(id);
  }

  public String throwError() {
    log.info("Throw error");
    throw new UnsupportedOperationException("Some dummy unsupported exception");
  }

  @Auditable(mode = LogMode.INVOCATION)
  public String processDataAuditable(String id) {
    log.info("Process data with id: {}", id);
    return "processed-data-%s".formatted(id);
  }

}
