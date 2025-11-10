package com.example.spring.framework.cache;

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

  @Cacheable(value = CacheId.MESSAGES)
  public String messageCall(String name1, String name2) {
    return api.expensiveMessageCall(name1, name2);
  }

  @Cacheable(value = CacheId.MESSAGES_MULTI_KEY, key = "#name1 + ':' + #name2")
  public String messageCallCustomCache(String name1, String name2) {
    return api.expensiveMessageCall(name1, name2);
  }
}
