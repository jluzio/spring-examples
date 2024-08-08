package com.example.spring.messaging.kafka.core.course.wikimedia;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
@ConfigurationProperties("app.course.wikimedia")
@Data
public class WikimediaConfigProps {

  private String recentChangeEndpoint;
}
