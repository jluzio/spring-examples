package com.example.spring.core.cache.spi;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.example.spring.core.cache.CachedCalculationService;
import com.example.spring.core.cache.ExpensiveCalculationApi;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

@SpringBootTest
@Slf4j
class CustomCacheTest {

  @Configuration
  @EnableCaching
  @Import({CachedCalculationService.class, ExpensiveCalculationApi.class})
  static class Config {

    @Bean
    public CacheManager cacheManager() {
      return new CustomCacheManager();
    }
  }

  @Autowired
  CacheManager cacheManager;
  @Autowired
  CachedCalculationService cachedCalculationService;
  @MockitoSpyBean
  ExpensiveCalculationApi expensiveCalculationApi;

  @Test
  void check_instance() {
    assertThat(cacheManager)
        .isInstanceOf(CustomCacheManager.class);
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
