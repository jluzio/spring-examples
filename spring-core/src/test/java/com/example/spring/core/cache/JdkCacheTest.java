package com.example.spring.core.cache;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@SpringBootTest
@Slf4j
class JdkCacheTest {

  @Configuration
  @EnableCaching
  @Import({CachedCalculationService.class, ExpensiveCalculationApi.class})
  static class Config {

    static boolean DYNAMIC_CACHES = true;

    @Bean
    public CacheManager cacheManager() {
      if (DYNAMIC_CACHES) {
        return new ConcurrentMapCacheManager();
      } else {
        return new ConcurrentMapCacheManager(CacheId.VALUES);
      }
    }
  }

  @Autowired
  org.springframework.cache.CacheManager cacheManager;
  @Autowired
  CachedCalculationService cachedCalculationService;
  @SpyBean
  ExpensiveCalculationApi expensiveCalculationApi;

  @Test
  void check_instance() {
    assertThat(cacheManager)
        .isInstanceOf(ConcurrentMapCacheManager.class);
  }

  @Test
  void basic_cache() {
    assertThat(cachedCalculationService.calculationCall())
        .isEqualTo("42");
    assertThat(cachedCalculationService.calculationCall())
        .isEqualTo("42");
    verify(expensiveCalculationApi, times(1)).expensiveCalculationCall();

    assertThat(cachedCalculationService.messageCall("World"))
        .isEqualTo("Hello World!");
    assertThat(cachedCalculationService.messageCall("John Doe"))
        .isEqualTo("Hello John Doe!");
    assertThat(cachedCalculationService.messageCall("World"))
        .isEqualTo("Hello World!");
    verify(expensiveCalculationApi, times(1)).expensiveMessageCall("World");
    verify(expensiveCalculationApi, times(1)).expensiveMessageCall("John Doe");
  }
}
