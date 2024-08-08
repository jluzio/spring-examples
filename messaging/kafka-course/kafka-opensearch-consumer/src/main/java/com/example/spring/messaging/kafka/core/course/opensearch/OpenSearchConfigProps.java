package com.example.spring.messaging.kafka.core.course.opensearch;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
@ConfigurationProperties("app.course.open-search")
@Data
public class OpenSearchConfigProps {

  private String scheme = "http";
  private String host = "localhost";
  private int port = 0;
  private String username;
  private String password;
  private String index;
}
