package com.example.spring.cloud.configclient;


import com.example.spring.cloud.configclient.config.model.SimpleValue;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/values")
public class SimpleValueController {

  @Value("${app.features.feat1.prop1}")
  private final String rawSimpleValue;
  private final SimpleValue simpleValue;
  private final SimpleValue refreshableSimpleValue;

  @GetMapping("/rawSimpleValue")
  public String rawSimpleValue() {
    return rawSimpleValue;
  }

  @GetMapping("/simpleValue")
  public String simpleValue() {
    return simpleValue.getValue();
  }

  @GetMapping("/refreshableSimpleValue")
  public String refreshableSimpleValue() {
    return refreshableSimpleValue.getValue();
  }
}
