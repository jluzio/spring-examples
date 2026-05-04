package com.example.spring.cloud.configclient.config.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class FeatureProperties {

  private String prop1;
  private String prop2;
  private String prop3;
}
