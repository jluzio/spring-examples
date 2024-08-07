package com.example.spring.messaging.kafka.spring.base;

import java.util.List;
import java.util.Map;
import lombok.extern.log4j.Log4j2;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.transaction.annotation.Transactional;

@Configuration
@ConditionalOnProperty(value = "app.words-config.default", havingValue = "true")
@Log4j2
public class WordsConfig {

  /*
    !!! NOTE !!!
    Spring Cloud Stream is setting key and value serializers with ByteArraySerializer.
    Using custom KafkaTemplate to use StringSerializer.
    Could also configure and use a RoutingKafkaTemplate.
   */

  @Bean
  KafkaTemplate<String, String> stringTemplate(ProducerFactory<String, String> producerFactory) {
    return new KafkaTemplate<>(
        producerFactory,
        Map.of(
            ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class,
            ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class
        ));
  }

  @Bean
  NewTopic newTopic() {
    return TopicBuilder.name("spring.words")
        .partitions(3)
        .replicas(1)
        .build();
  }

  @Bean
  @ConditionalOnProperty(value = "app.word-config.producers", havingValue = "true")
  public ApplicationRunner runner(KafkaTemplate<String, String> template) {
    return args -> template.send("spring.words", "test");
  }

  @KafkaListener(id = "myWordsConsumer", topics = "spring.words")
  public void listener(String payload) {
    log.debug("listener: {}", payload);
  }

  @KafkaListener(id = "anotherConsumer", topics = "spring.words")
  // Can also use transactions
  @Transactional
  public void listenerWithHeaders(
      @Payload String payload, @Header(KafkaHeaders.GROUP_ID) String groupId) {
    log.debug("listenerWithHeaders :: {} | {}", payload, groupId);
  }

  // Added 'batch' and 'value.deserializer', since it was receiving as List<Integer>.
  // Maybe due to spring-cloud-string (?)
  @KafkaListener(
      id = "myBatchWordsConsumer",
      topics = "spring.words"
      , batch = "true"
      , properties = {
      ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG
          + "=org.apache.kafka.common.serialization.StringDeserializer"
  }
  )
  public void listenerBatch(List<String> payloads) {
    log.debug("listenerBatch :: {}", payloads);
  }

}
