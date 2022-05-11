package com.example.spring.core.cache;

import org.springframework.stereotype.Component;

@Component
public class ExpensiveCalculationApi {

  public String expensiveCalculationCall() {
    return "42";
  }

  public String expensiveMessageCall(String name) {
    return "Hello %s!".formatted(name);
  }
}
