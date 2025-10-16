package com.example.spring.core.lib.resilience4j;

import java.util.function.Predicate;

public class RecordFailurePredicate implements Predicate<Throwable> {

  @Override
  public boolean test(Throwable throwable) {
    return !(throwable instanceof BusinessException);
  }
}