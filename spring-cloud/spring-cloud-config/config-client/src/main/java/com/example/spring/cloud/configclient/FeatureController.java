package com.example.spring.cloud.configclient;

import com.example.spring.cloud.configclient.config.Features;
import com.example.spring.cloud.configclient.config.model.FeatureProperties;
import java.util.Map;
import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/features")
public class FeatureController {

  private final Map<String, FeatureProperties> featurePropertiesMap;

  @GetMapping
  public Map<String, FeatureProperties> getFeaturePropertiesMap() {
    return Features.cloneFeaturePropertiesMap(featurePropertiesMap);
  }

  @GetMapping("{id}")
  public FeatureProperties getFeatureProperties(@PathVariable String id) {
    if (!featurePropertiesMap.containsKey(id)) {
      throw new NoSuchElementException(id);
    }
    return Features.cloneFeatureProperties(featurePropertiesMap.get(id));
  }
}