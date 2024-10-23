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
import java.util.concurrent.atomic.AtomicInteger;
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
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@SpringBootTest(
    properties = {
        "spring.cache.cache-names=default,hello,book,crud",
        "app.cache.templateSpec=maximumSize=500,expireAfterWrite=PT10S,expireAfterAccess=PT60S,recordStats",
        "app.cache.specs.hello=maximumSize=50,expireAfterWrite=PT10S,recordStats",
    }
)
@Slf4j
class CachePlaygroundTest {

  @Configuration
  @EnableCaching
  @Import({PlaygroundService.class, BasicCrudService.class})
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
  BasicCrudService basicCrudService;
  @Autowired
  PlaygroundService playgroundService;
  @Autowired
  CacheManager cacheManager;

  @Test
  void test_basic_crud() {
    var id1 = "id1";
    var id2 = "id2";

    var valueId1 = basicCrudService.get(id1);
    assertThat(valueId1)
        .isEqualTo(basicCrudService.get(id1))
        .isNotEqualTo(basicCrudService.get(id2));

    basicCrudService.put(id1, "new-value");
    var valueId1Update1 = basicCrudService.get(id1);
    assertThat(valueId1Update1)
        .isNotEqualTo(valueId1);

    basicCrudService.evict(id1);
    var valueId1Update2 = basicCrudService.get(id1);
    assertThat(valueId1Update2)
        .isNotEqualTo(valueId1Update1);
  }

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
        .doesNotContainEntry("expireAfterAccess", Optional.empty());
    ;
    assertThat(helloNativeCachePolicy)
        .containsEntry("expireAfterAccess", Optional.empty());
  }

  @Test
  void test_stats() {
    var cache = cacheManager.getCache("hello");
    var nativeCache = nativeCache(cache);

    log.debug("stats: {}", nativeCache.stats());

    assertThat(playgroundService.sayHello("target1"))
        .isEqualTo(playgroundService.sayHello("target1"));

    log.debug("stats: {}", nativeCache.stats());
    assertThat(nativeCache.stats().hitCount())
        .isOne();
  }

  @Test
  void test_same_cache_different_methods_different_keys() {
    assertThat(playgroundService.defaultValue("name1"))
        .isEqualTo(playgroundService.defaultValue("name1"));
    assertThat(playgroundService.defaultValue(1))
        .isEqualTo(playgroundService.defaultValue(1));
  }

  @Test
  void test_conditional_and_unless() {
    var cache = cacheManager.getCache("book");
    var nativeCache = nativeCache(cache);

    var bookNameShortestPaperback = playgroundService.books().stream()
        .filter(Predicates.not(Book::hardback))
        .map(Book::name)
        .min(Comparator.comparing(String::length))
        .orElseThrow();
    var bookNameHardcover = playgroundService.books().stream()
        .filter(Book::hardback)
        .map(Book::name)
        .findAny()
        .orElseThrow();
    var bookNameLargest = playgroundService.books().stream()
        .map(Book::name)
        .max(Comparator.comparing(String::length))
        .orElseThrow();

    assertThat(playgroundService.findBook(bookNameShortestPaperback)).isNotNull();
    assertThat(playgroundService.findBook(bookNameShortestPaperback)).isNotNull();
    assertThat(nativeCache.stats().hitCount())
        .isOne();
    assertThat(nativeCache.stats().missCount())
        .isOne();

    assertThat(playgroundService.findBook(bookNameLargest)).isNotNull();
    assertThat(playgroundService.findBook(bookNameLargest)).isNotNull();
    assertThat(nativeCache.stats().hitCount())
        .isOne();
    assertThat(nativeCache.stats().missCount())
        .isOne();

    assertThat(playgroundService.findBook(bookNameHardcover)).isNotNull();
    assertThat(playgroundService.findBook(bookNameHardcover)).isNotNull();
    assertThat(nativeCache.stats().hitCount())
        .isOne();
    assertThat(nativeCache.stats().missCount())
        .isEqualTo(3);
  }


  @SuppressWarnings("unchecked")
  private com.github.benmanes.caffeine.cache.Cache<Object, Object> nativeCache(Cache cache) {
    return (com.github.benmanes.caffeine.cache.Cache<Object, Object>) cache.getNativeCache();
  }

  @CacheConfig(cacheNames = "crud")
  static class BasicCrudService {

    @Builder(toBuilder = true)
    record CrudData(String id, String value) {

    }

    AtomicInteger counter = new AtomicInteger();

    @Cacheable
    public CrudData get(String id) {
      CrudData value = new CrudData(id, String.valueOf(counter.incrementAndGet()));
      log.debug("get[{}] = {}", id, value);
      return value;
    }

    @CacheEvict
    public void evict(String id) {
      log.debug("evict: {}", id);
    }

    @CachePut(key = "#id")
    public CrudData put(String id, String partialValue) {
      CrudData value = new CrudData(id, partialValue);
      log.debug("put[{}] = {}", id, value);
      return value;
    }
  }

  static class PlaygroundService {

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
