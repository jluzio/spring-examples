package com.example.spring.core.cache;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.spring.core.cache.CachePlaygroundTest.Config.CacheProperties;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.Policy;
import com.github.benmanes.caffeine.cache.Policy.FixedExpiration;
import com.google.common.base.Predicates;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@SpringBootTest(
    properties = {
        "spring.cache.cache-names=default,hello,book",
        "app.cache.templateSpec=maximumSize=500,expireAfterWrite=PT10S,expireAfterAccess=PT60S,recordStats",
        "app.cache.specs.hello=maximumSize=50,expireAfterWrite=PT10S,recordStats",
    }
)
@Slf4j
class CachePlaygroundTest {

  @Configuration
  @EnableCaching
  @Import({CacheableService.class})
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
  CacheableService service;
  @Autowired
  CacheManager cacheManager;

  @Test
  void test_customCache_does_not_inherit_template() {
    Function<Policy<?, ?>, Map<String, Object>> policyConfigSubset = policy ->
        Map.of(
            "recordingStats", policy.isRecordingStats(),
            "expireAfterWrite", policy.expireAfterWrite().map(FixedExpiration::getExpiresAfter),
            "expireAfterAccess", policy.expireAfterAccess().map(FixedExpiration::getExpiresAfter)
        );

    var defaultCache = cacheManager.getCache("default");
    var defaultNativeCache = nativeCache(defaultCache);
    var defaultNativeCachePolicy = policyConfigSubset.apply(defaultNativeCache.policy());
    var helloCache = cacheManager.getCache("hello");
    var helloNativeCache = nativeCache(helloCache);
    var helloNativeCachePolicy = policyConfigSubset.apply(helloNativeCache.policy());

    log.debug("cache default :: cache={} | nativeCache={}\n| nativeCache.policy={}",
        defaultCache, defaultNativeCache, defaultNativeCachePolicy);
    log.debug("cache hello :: cache={} | nativeCache={}\n| nativeCache.policy={}",
        helloCache, helloNativeCache, helloNativeCachePolicy);

    assertThat(defaultNativeCachePolicy)
        .doesNotContainEntry("expireAfterAccess", Optional.empty());;
    assertThat(helloNativeCachePolicy)
        .containsEntry("expireAfterAccess", Optional.empty());
  }

  @Test
  void test_stats() {
    var cache = cacheManager.getCache("hello");
    var nativeCache = nativeCache(cache);

    log.debug("stats: {}", nativeCache.stats());

    assertThat(service.sayHello("target1"))
        .isEqualTo(service.sayHello("target1"));

    log.debug("stats: {}", nativeCache.stats());
    assertThat(nativeCache.stats().hitCount())
        .isOne();
  }

  @Test
  void test_same_cache_different_methods_different_keys() {
    assertThat(service.defaultValue("name1"))
        .isEqualTo(service.defaultValue("name1"));
    assertThat(service.defaultValue(1))
        .isEqualTo(service.defaultValue(1));
  }

  @Test
  void test_conditional_and_unless() {
    var cache = cacheManager.getCache("book");
    var nativeCache = nativeCache(cache);

    var bookNameShortestPaperback = service.books().stream()
        .filter(Predicates.not(Book::hardback))
        .map(Book::name)
        .min(Comparator.comparing(String::length))
        .orElseThrow();
    var bookNameHardcover = service.books().stream()
        .filter(Book::hardback)
        .map(Book::name)
        .findAny()
        .orElseThrow();
    var bookNameLargest = service.books().stream()
        .map(Book::name)
        .max(Comparator.comparing(String::length))
        .orElseThrow();

    assertThat(service.findBook(bookNameShortestPaperback)).isNotNull();
    assertThat(service.findBook(bookNameShortestPaperback)).isNotNull();
    assertThat(nativeCache.stats().hitCount())
        .isOne();
    assertThat(nativeCache.stats().missCount())
        .isOne();

    assertThat(service.findBook(bookNameLargest)).isNotNull();
    assertThat(service.findBook(bookNameLargest)).isNotNull();
    assertThat(nativeCache.stats().hitCount())
        .isOne();
    assertThat(nativeCache.stats().missCount())
        .isOne();

    assertThat(service.findBook(bookNameHardcover)).isNotNull();
    assertThat(service.findBook(bookNameHardcover)).isNotNull();
    assertThat(nativeCache.stats().hitCount())
        .isOne();
    assertThat(nativeCache.stats().missCount())
        .isEqualTo(3);
  }


  @SuppressWarnings("unchecked")
  private com.github.benmanes.caffeine.cache.Cache<Object, Object> nativeCache(Cache cache) {
    return (com.github.benmanes.caffeine.cache.Cache<Object, Object>) cache.getNativeCache();
  }

  static class CacheableService {

    @Cacheable("hello")
    public String sayHello(String who) {
      return "Hello %s @ %s".formatted(who, LocalDateTime.now());
    }

    @Cacheable("default")
    public String defaultValue(String name) {
      return "name-%s".formatted(Instant.now().getEpochSecond());
    }

    @Cacheable("default")
    public String defaultValue(int id) {
      return "id-%s".formatted(Instant.now().getEpochSecond());
    }

    List<Book> books = List.of(
        new Book(UUID.randomUUID().toString(), "Cache'r'us", false),
        new Book(UUID.randomUUID().toString(), "Paperback book", false),
        new Book(UUID.randomUUID().toString(), "Hardcover book", true),
        new Book(UUID.randomUUID().toString(), "Longest title by a long shot", false)
    );

    public List<Book> books() {
      return books;
    }

    @Cacheable(cacheNames = "book", condition = "#name.length() < 15", unless = "#result.hardback")
    public Book findBook(String name) {
      return books.stream()
          .filter(it -> Objects.equals(it.name(), name))
          .findFirst()
          .orElse(null);
    }

    @Cacheable(cacheNames = "book", condition = "#name.length() < 15", unless = "#result?.hardback")
    public Optional<Book> findBookOptional(String name) {
      return books.stream()
          .filter(it -> Objects.equals(it.name(), name))
          .findFirst();
    }
  }

  record Book(String id, String name, boolean hardback) {

  }
}
