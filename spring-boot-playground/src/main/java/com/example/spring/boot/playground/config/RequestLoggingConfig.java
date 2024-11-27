package com.example.spring.boot.playground.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.CommonsRequestLoggingFilter;

@Configuration
public class RequestLoggingConfig {

  @Bean
  @ConditionalOnMissingBean({CommonsRequestLoggingFilter.class})
  @ConditionalOnProperty(value = "app.http.request-logging-filter.enabled", havingValue = "true")
  @ConfigurationProperties("app.http.request-logging-filter")
  public CommonsRequestLoggingFilter commonsRequestLoggingFilter() {
    CommonsRequestLoggingFilter loggingFilter = new CommonsRequestLoggingFilter();
    loggingFilter.setIncludeClientInfo(true);
    loggingFilter.setIncludeHeaders(true);
    loggingFilter.setIncludePayload(true);
    loggingFilter.setIncludeQueryString(true);
    return loggingFilter;
  }
}
