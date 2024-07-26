package com.example.spring.messaging.kafka.course.wikimedia;

import jakarta.annotation.PreDestroy;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.Disposable;

@RequiredArgsConstructor
public class WikimediaChangesProducer implements ApplicationRunner {

  private final WikimediaConfig config;
  private KafkaProducer<String, String> producer;
  private Disposable wikimediaSubscriber;

  @Override
  public void run(ApplicationArguments args) throws Exception {
    Map<String, Object> kafkaConfig = Map.of(
        CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG, config.getBootstrapServers(),
        ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName(),
        ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName()
    );

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
