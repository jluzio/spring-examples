package com.example.spring.cloud.configclient;


import com.example.spring.cloud.configclient.config.model.SimpleValue;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/values")
public class SimpleValueController {

  private final SimpleValue simpleValue;
  private final SimpleValue refreshableSimpleValue;

  @GetMapping("/simpleValue")
  public String simpleValue() {
    return simpleValue.getValue();
  }

  @GetMapping("/simpleValue/refreshable")
  public String refreshableSimpleValue() {
    return refreshableSimpleValue.getValue();
  }
}
