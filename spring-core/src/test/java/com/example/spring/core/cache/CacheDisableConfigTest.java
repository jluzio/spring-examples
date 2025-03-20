package com.example.spring.core.cache;

import static org.assertj.core.api.Assertions.assertThat;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Slf4j
class CacheDisableConfigTest {

  @Configuration
  @Import({CacheConfig.class})
  static class Config {

  }

  @EnableCaching
  @ConditionalOnExpression("'${spring.cache.type}' != 'none'")
  static class CacheConfig {

    @Bean
    CacheManager cacheManager() {
      return new ConcurrentMapCacheManager();
    }
  }

  @Test
  void test_cache_enabled() {
    try (var app = app(true)) {
      var cacheManagerBeansMap = app.getBeansOfType(CacheManager.class);
      assertThat(cacheManagerBeansMap)
          .isNotEmpty()
          .hasSize(1);
    }
  }

  @Test
  void test_cache_disabled() {
    try (var app = app(false)) {
      var cacheManagerBeansMap = app.getBeansOfType(CacheManager.class);
      assertThat(cacheManagerBeansMap)
          .isEmpty();
    }
  }

  ConfigurableApplicationContext app(boolean springCacheEnabled) {
    var springCacheType = springCacheEnabled
        ? ""
        : "spring.cache.type=none";
    return new SpringApplicationBuilder(Config.class)
        .properties(springCacheType)
        .web(WebApplicationType.NONE)
        .run();
  }
}
