package com.example.spring.core.cache;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.github.benmanes.caffeine.cache.Caffeine;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@SpringBootTest(classes = {CaffeineCacheTest.Config.class})
@Slf4j
class CaffeineCacheTest {

  @Configuration
  @EnableCaching
  @Import({CachedCalculationService.class, ExpensiveCalculationApi.class})
  static class Config {

    @Bean
    public Caffeine<Object, Object> caffeineConfig() {
      return Caffeine.newBuilder()
          .expireAfterWrite(60, TimeUnit.MINUTES)
          .recordStats();
    }

    @Bean
    public CacheManager cacheManager(Caffeine<Object, Object> caffeine) {
      var cacheManager = new CaffeineCacheManager();
      cacheManager.setCaffeine(caffeine);
      return cacheManager;
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
        .isInstanceOf(CaffeineCacheManager.class);
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

    Arrays.stream(CacheId.VALUES)
        .forEach(name -> log.info("stats[{}]: {}", name, getNativeCache(name).stats()));
  }

  private com.github.benmanes.caffeine.cache.Cache<?, ?> getNativeCache(String name) {
    var springCache = cacheManager.getCache(name);
    return (com.github.benmanes.caffeine.cache.Cache<?, ?>) springCache.getNativeCache();
  }

}
