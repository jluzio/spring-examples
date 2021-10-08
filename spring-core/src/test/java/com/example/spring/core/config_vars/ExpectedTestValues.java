package com.example.spring.core.config_vars;

import com.example.spring.core.config_vars.ExampleServiceConfig.Feature;
import java.util.Map;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ExpectedTestValues {

  public ExampleServiceConfig exampleServiceConfig() {
    return ExampleServiceConfig.builder()
        .enabled(true)
        .name("example-service")
        .endpoint("http://example-service:8080/v1")
        .features(Map.of(
            "text",
            Feature.builder()
                .name("text")
                .values(Map.of("from", "+351000000000"))
                .build(),
            "email",
            Feature.builder()
                .name("email")
                .values(Map.of("from", "john.doe@server.com"))
                .build()
        ))
        .build();
  }

}
