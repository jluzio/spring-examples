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

  public String expensiveMessageCall(String name1, String name2) {
    return "Hello %s and %s!".formatted(name1, name2);
  }
}
