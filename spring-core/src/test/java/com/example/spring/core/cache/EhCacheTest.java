package com.example.spring.core.cache;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.Arrays;
import javax.cache.Caching;
import lombok.extern.slf4j.Slf4j;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.jsr107.Eh107Configuration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.jcache.JCacheCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@SpringBootTest
@Slf4j
class EhCacheTest {

  @Configuration
  @EnableCaching
  @Import({CachedCalculationService.class, ExpensiveCalculationApi.class})
  static class Config {

    @Bean
    org.springframework.cache.CacheManager cacheManager(
        javax.cache.CacheManager nativeCacheManager) {
      return new JCacheCacheManager(nativeCacheManager);
    }

    @Bean
    javax.cache.CacheManager nativeCacheManager() {
      var cacheProvider = Caching.getCachingProvider();
      var cacheManager = cacheProvider.getCacheManager();
      var templateConfigurationBuilder = CacheConfigurationBuilder.newCacheConfigurationBuilder(
          Object.class,
          Object.class,
          ResourcePoolsBuilder.heap(10)
      );
      Arrays.stream(CacheId.VALUES)
          .forEach(name -> {
            var configuration = Eh107Configuration.fromEhcacheCacheConfiguration(
                templateConfigurationBuilder.build());
            cacheManager.createCache(name, configuration);
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

  @Test
  void check_instance() {
    assertThat(cacheManager)
        .isInstanceOf(JCacheCacheManager.class);
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
