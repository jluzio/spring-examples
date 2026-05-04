package com.example.spring.cloud.configclient.config;

import com.example.spring.cloud.configclient.config.model.FeatureProperties;
import java.util.Map;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeaturesConfig {

  @Bean
  public Map<String, FeatureProperties> computedFeaturePropertiesMap(Map<String, FeatureProperties> featuresConfig) {
    return Features.cloneFeaturePropertiesMap(featuresConfig);
  }

  @Bean
  @RefreshScope
  public Map<String, FeatureProperties> refreshableComputedFeaturePropertiesMap(
      Map<String, FeatureProperties> featuresConfig) {
    return Features.cloneFeaturePropertiesMap(featuresConfig);
  }
}
