package com.example.spring.messaging.kafka.spring;

import java.util.List;
import java.util.stream.IntStream;
import lombok.extern.log4j.Log4j2;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.transaction.annotation.Transactional;

@Configuration
@ConditionalOnProperty(value = "app.kafka-apps.words.enabled", havingValue = "true")
@Log4j2
public class WordsConfig {

  @Bean
  NewTopic newTopic() {
    return TopicBuilder.name("spring.words")
        .partitions(3)
        .replicas(1)
        .build();
  }

  @Bean
  @ConditionalOnProperty(value = "app.kafka-apps.words.producers.enabled", havingValue = "true")
  public ApplicationRunner runner(KafkaTemplate<Object, Object> template) {
    return args -> IntStream.rangeClosed(1, 2).forEach(v ->
        template.send("spring.words", "test-" + v));
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

  @KafkaListener(
      id = "myBatchWordsConsumer",
      topics = "spring.words",
      batch = "true"
  )
  public void listenerBatch(List<String> payloads) {
    log.debug("listenerBatch :: {}", payloads);
  }

}
