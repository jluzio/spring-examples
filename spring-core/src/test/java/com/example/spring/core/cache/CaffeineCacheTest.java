package com.example.spring.core.cache;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.example.spring.core.cache.CaffeineCacheTest.Config.CacheProperties;
import com.github.benmanes.caffeine.cache.Caffeine;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Stream;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import reactor.core.publisher.Mono;

@SpringBootTest(
    properties = {
        "app.cache.specs.CALCULATIONS=expireAfterWrite=PT0.1S",
        "app.cache.specs.MESSAGES=expireAfterWrite=PT1S",
    }
)
@Slf4j
class CaffeineCacheTest {

  @Configuration
  @EnableCaching
  @Import({CachedCalculationService.class, ExpensiveCalculationApi.class})
  @EnableConfigurationProperties(CacheProperties.class)
  static class Config {

    @ConfigurationProperties(prefix = "app.cache")
    @Builder(toBuilder = true)
    public record CacheProperties(String templateSpec, Map<String, String> specs) {

    }

    @Bean
    public CacheManager cacheManager(CacheProperties properties) {
      var cacheManager = new CaffeineCacheManager();
      cacheManager.setCaffeine(Caffeine.from(properties.templateSpec()));
      properties.specs().forEach((key, value) -> {
        var cache = new CaffeineCache(
            key,
            Caffeine.from(value).build()
        );
        cacheManager.registerCustomCache(key, cache.getNativeCache());
      });
      return cacheManager;
    }
  }

  @Autowired
  org.springframework.cache.CacheManager cacheManager;
  @Autowired
  CachedCalculationService cachedCalculationService;
  @SpyBean
  ExpensiveCalculationApi expensiveCalculationApi;

  @BeforeEach
  void clearCashes() {
    cacheManager.getCacheNames().stream()
        .flatMap(name -> Stream.ofNullable(cacheManager.getCache(name)))
        .forEach(Cache::clear);
  }

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

  @Test
  void basic_cache_multi_key() {
    assertThat(cachedCalculationService.messageCall("Foo", "Bar"))
        .isEqualTo("Hello Foo and Bar!");
    assertThat(cachedCalculationService.messageCall("Foo", "Bar"))
        .isEqualTo("Hello Foo and Bar!");
    assertThat(cachedCalculationService.messageCall("Foo", "Baz"))
        .isEqualTo("Hello Foo and Baz!");
    verify(expensiveCalculationApi, times(1)).expensiveMessageCall("Foo", "Bar");
    verify(expensiveCalculationApi, times(1)).expensiveMessageCall("Foo", "Baz");

    Arrays.stream(CacheId.VALUES)
        .forEach(name -> log.info("stats[{}]: {}", name, getNativeCache(name).stats()));
  }

  @Test
  void basic_cache_multi_key_customCache() {
    assertThat(cachedCalculationService.messageCallCustomCache("Foo", "Bar"))
        .isEqualTo("Hello Foo and Bar!");
    assertThat(cachedCalculationService.messageCallCustomCache("Foo", "Bar"))
        .isEqualTo("Hello Foo and Bar!");
    assertThat(cachedCalculationService.messageCallCustomCache("Foo", "Baz"))
        .isEqualTo("Hello Foo and Baz!");
    verify(expensiveCalculationApi, times(1)).expensiveMessageCall("Foo", "Bar");
    verify(expensiveCalculationApi, times(1)).expensiveMessageCall("Foo", "Baz");

    Arrays.stream(CacheId.VALUES)
        .forEach(name -> log.info("stats[{}]: {}", name, getNativeCache(name).stats()));
  }

  @Test
  void expiry() {
    var startTime = LocalDateTime.now();
    assertThat(cachedCalculationService.calculationCall())
        .isEqualTo("42");
    assertThat(cachedCalculationService.calculationCall())
        .isEqualTo("42");
    verify(expensiveCalculationApi, times(1)).expensiveCalculationCall();

    var currentDurationMillis = Duration.between(startTime, LocalDateTime.now()).toMillis();
    assertThat(currentDurationMillis)
        .isLessThan(500);
    Mono.delay(Duration.of(600 - currentDurationMillis, ChronoUnit.MILLIS))
        .block();
    assertThat(cachedCalculationService.calculationCall())
        .isEqualTo("42");
    verify(expensiveCalculationApi, times(2)).expensiveCalculationCall();
  }

  private com.github.benmanes.caffeine.cache.Cache<?, ?> getNativeCache(String name) {
    var springCache = cacheManager.getCache(name);
    return (com.github.benmanes.caffeine.cache.Cache<?, ?>) springCache.getNativeCache();
  }

}
