package com.example.spring.cloud.configclient;


import com.example.spring.cloud.configclient.config.model.SimpleValue;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/values", produces = MediaType.TEXT_PLAIN_VALUE)
public class ValuesController {

  private final SimpleValue simpleValue;
  private final SimpleValue refreshableSimpleValue;
  private final Environment environment;

  @GetMapping("/simpleValue")
  public String simpleValue() {
    return simpleValue.getValue();
  }

  @GetMapping("/simpleValue/refreshable")
  public String refreshableSimpleValue() {
    return refreshableSimpleValue.getValue();
  }

  @GetMapping("/keys/{key}")
  public String customKeyValue(@PathVariable String key) {
    return environment.getProperty(key);
  }
}
