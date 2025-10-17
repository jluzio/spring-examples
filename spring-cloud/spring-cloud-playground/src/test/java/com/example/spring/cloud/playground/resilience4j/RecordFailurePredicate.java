package com.example.spring.cloud.playground.resilience4j;

import java.util.function.Predicate;

public class RecordFailurePredicate implements Predicate<Throwable> {

  @Override
  public boolean test(Throwable throwable) {
    return !(throwable instanceof BusinessException);
  }
}