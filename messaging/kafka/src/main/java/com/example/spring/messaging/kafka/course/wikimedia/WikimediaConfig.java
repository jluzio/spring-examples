package com.example.spring.messaging.kafka.course.wikimedia;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Profile;

@ConfigurationProperties("app.course.wikimedia")
@Profile("wikimedia")
@Data
public class WikimediaConfig {

  @Value("${spring.cloud.stream.kafka.binder.brokers}")
  private String bootstrapServers;
  private String recentChangeEndpoint;
  private String topic;
  private int partitions;

}
