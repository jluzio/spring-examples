package com.example.spring.cloud.configclient;

import com.example.spring.cloud.configclient.config.model.FeatureProperties;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/features/computed")
public class ComputedFeaturePropertiesController {

  private final Map<String, FeatureProperties> computedFeaturePropertiesMap;
  private final Map<String, FeatureProperties> refreshableComputedFeaturePropertiesMap;

  @GetMapping
  public Map<String, FeatureProperties> getComputedFeaturePropertiesMap() {
    return computedFeaturePropertiesMap;
  }

  @GetMapping("/refreshable")
  public Map<String, FeatureProperties> getRefreshableComputedFeaturePropertiesMap() {
    return refreshableComputedFeaturePropertiesMap;
  }
}