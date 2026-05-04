package com.example.spring.cloud.configclient;

import com.example.spring.cloud.configclient.config.FeaturesConfig;
import com.example.spring.cloud.configclient.config.model.FeatureProperties;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RefreshScope
@RequiredArgsConstructor
public class ComputedPropertiesController {

  private final FeaturesConfig featuresConfig;

  @GetMapping("/features/computed")
  public Map<String, FeatureProperties> getComputedFeaturePropertiesMap() {
    return featuresConfig.computedFeaturePropertiesMap(null);
  }

  @GetMapping("/features/computed-refresh")
  public Map<String, FeatureProperties> getRefreshableComputedFeaturePropertiesMap() {
    return featuresConfig.refreshableComputedFeaturePropertiesMap(null);
  }
}