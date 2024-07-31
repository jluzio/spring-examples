package com.example.spring.messaging.kafka.course.opensearch;

import jakarta.annotation.PreDestroy;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.util.unit.DataSize;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.Disposable;

@RequiredArgsConstructor
public class OpenSearchConsumer implements ApplicationRunner {

  private final OpenSearchConfig config;
  private KafkaProducer<String, String> producer;
  private Disposable wikimediaSubscriber;

  @Override
  public void run(ApplicationArguments args) throws Exception {
    Map<String, Object> kafkaConfig = config.kafkaConfig(Map.of(
        // high throughput config
        ProducerConfig.COMPRESSION_TYPE_CONFIG, "snappy",
        ProducerConfig.LINGER_MS_CONFIG, 20,
        ProducerConfig.BATCH_SIZE_CONFIG, (int) DataSize.ofKilobytes(32).toBytes()
    ));

    // create topic
    var admin = new KafkaAdmin(kafkaConfig);
    admin.createOrModifyTopics(new NewTopic(config.getTopic(), config.getPartitions(), (short) 1));

    producer = new KafkaProducer<>(kafkaConfig);

    wikimediaSubscriber = WebClient.create(config.getRecentChangeEndpoint())
        .get()
        .retrieve()
        .bodyToFlux(new ParameterizedTypeReference<ServerSentEvent<String>>() {
        })
        .doOnNext(event -> {
          var producerRecord = new ProducerRecord<>(
              config.getTopic(), event.id(), event.data());
          producer.send(producerRecord);
        })
        .subscribe();
  }

  @PreDestroy
  public void shutdown() {
    wikimediaSubscriber.dispose();
    producer.close();
  }
}
