package com.example.spring.cloud.playground.feign;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.context.annotation.Bean;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

/**
 * Configuration to support WebFlux, since it doesn't support reactive styles.
 * <p>Note: this configures blocking messageConverters, not reactive ones.</p>
 * <p>Otherwise use the reactive libraries such as <code>feign-reactive</code></p>
 * @see  <a href="https://github.com/PlaytikaOSS/feign-reactive">feign-reactive</a>
 */
public class FeignWebFluxConfig {

  @Bean
  @ConditionalOnMissingBean
  public HttpMessageConverters messageConverters(ObjectProvider<HttpMessageConverter<?>> converters) {
    return new HttpMessageConverters(converters.orderedStream().toList());
  }

  @Bean
  @ConditionalOnMissingBean(value = MappingJackson2HttpMessageConverter.class)
  MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter(ObjectMapper objectMapper) {
    return new MappingJackson2HttpMessageConverter(objectMapper);
  }
}
