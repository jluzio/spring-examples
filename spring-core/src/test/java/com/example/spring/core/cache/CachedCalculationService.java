package com.example.spring.core.cache;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CachedCalculationService {

  private final ExpensiveCalculationApi api;

  @Cacheable(CacheId.CALCULATIONS)
  public String calculationCall() {
    return api.expensiveCalculationCall();
  }

  @Cacheable(CacheId.MESSAGES)
  public String messageCall(String name) {
    return api.expensiveMessageCall(name);
  }
}
