package com.example.spring.core.config_vars;

import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "example-service.settings")
@Data
@RequiredArgsConstructor
@AllArgsConstructor
@Builder
public class ExampleServiceConfig {

  private Boolean enabled;
  private String id;
  private String name;
  private String endpoint;
  private Map<String, Feature> features;

  @Data
  @RequiredArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class Feature {

    private String name;
    private Map<String, String> values;

  }
}
