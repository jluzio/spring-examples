package com.example.liquibase.tools.cache;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.liquibase.tools.cache.CacheTest.ExpensiveCallService;
import com.example.liquibase.tools.config.CacheConfiguration;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

@SpringBootTest(classes = {ExpensiveCallService.class, CacheConfiguration.class})
class CacheTest {

  @Autowired
  private ExpensiveCallService expensiveCallService;

  @Component
  @Slf4j
  public static class ExpensiveCallService {

    AtomicInteger counter = new AtomicInteger();

    @Cacheable("expensive_calls")
    public Integer call(String parameter) {
      log.info("calling real method with parameter {}", parameter);
      return counter.incrementAndGet();
    }
  }

  @Test
  void cacheTest() {
    String param1 = "param1";
    String param2 = "param2";

    assertThat(expensiveCallService.call(param1)).isEqualTo(1);
    assertThat(expensiveCallService.call(param1)).isEqualTo(1);
    assertThat(expensiveCallService.call(param1)).isEqualTo(1);
    assertThat(expensiveCallService.call(param2)).isEqualTo(2);
    assertThat(expensiveCallService.call(param2)).isEqualTo(2);
    assertThat(expensiveCallService.call(param1)).isEqualTo(1);
  }

}
