package com.example.spring.messaging.kafka.course.basics.config;

import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;

@Getter
@Accessors(fluent = true)
public class AppConfig {

  @Value("${spring.cloud.stream.kafka.binder.brokers}")
  private String bootstrapServers;

  private final String defaultTopic = "demo_topic";

  public Map<String, Object> basicKafkaConfig() {
    return Map.of(
        CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers,
        // Producer config
        ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName(),
        ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName(),
        // Consumer config
        ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName(),
        ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName()
    );
  }

  public Map<String, Object> kafkaConfig(Map<String, Object> extraConfig) {
    var config = new HashMap<>(basicKafkaConfig());
    config.putAll(extraConfig);
    return config;
  }

}
