package com.example.spring.cloud.configclient.config;

import com.example.spring.cloud.configclient.config.model.FeatureProperties;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.experimental.UtilityClass;

@UtilityClass
public class Features {

  public static Map<String, FeatureProperties> cloneFeaturePropertiesMap(Map<String, FeatureProperties> featurePropertiesMap) {
    return featurePropertiesMap.entrySet().stream()
        .sorted(Map.Entry.comparingByKey())
        .collect(Collectors.toMap(
            Map.Entry::getKey,
            e -> cloneFeatureProperties(e.getValue()),
            (existing, replacement) -> existing,
            LinkedHashMap::new
        ));
  }

  public static FeatureProperties cloneFeatureProperties(FeatureProperties featureProperties) {
    return featureProperties.toBuilder().build();
  }
}
