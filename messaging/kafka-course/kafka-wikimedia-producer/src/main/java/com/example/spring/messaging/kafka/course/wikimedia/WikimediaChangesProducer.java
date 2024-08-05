package com.example.spring.messaging.kafka.course.wikimedia;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;
import jakarta.annotation.PreDestroy;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.stereotype.Component;
import org.springframework.util.unit.DataSize;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.Disposable;

@Component
@RequiredArgsConstructor
@Log4j2
public class WikimediaChangesProducer implements ApplicationRunner {

  private final KafkaConfigProps kafkaConfigProps;
  private final WikimediaConfigProps wikimediaConfigProps;
  private final ObjectMapper objectMapper;
  private KafkaProducer<String, String> producer;
  private Disposable wikimediaSubscriber;

  @Override
  public void run(ApplicationArguments args) throws Exception {
    Map<String, Object> kafkaConfig = kafkaConfigProps.kafkaConfig(Map.of(
        // high throughput config
        ProducerConfig.COMPRESSION_TYPE_CONFIG, "snappy",
        ProducerConfig.LINGER_MS_CONFIG, 20,
        ProducerConfig.BATCH_SIZE_CONFIG, (int) DataSize.ofKilobytes(32).toBytes()
    ));

    // create topic
    var admin = new KafkaAdmin(kafkaConfig);
    admin.createOrModifyTopics(new NewTopic(kafkaConfigProps.getTopic(), kafkaConfigProps.getPartitions(), (short) 1));

    producer = new KafkaProducer<>(kafkaConfig);

    wikimediaSubscriber = WebClient.create(wikimediaConfigProps.getRecentChangeEndpoint())
        .get()
        .retrieve()
        .bodyToFlux(new ParameterizedTypeReference<ServerSentEvent<String>>() {
        })
        .doOnNext(event -> {
          String eventData = event.data();
          if (Strings.isNullOrEmpty(eventData)) {
            log.trace("Ignoring message without data");
          } else {
            var eventDataId = getEventDataId(event);
            var producerRecord = new ProducerRecord<>(
                kafkaConfigProps.getTopic(), eventDataId, eventData);
            producer.send(producerRecord);
          }
        })
        .subscribe();
  }

  private String getEventDataId(ServerSentEvent<String> event) {
    try {
      var eventDataNode = objectMapper.readTree(event.data());
      return eventDataNode.at("/meta/id").asText();
    } catch (JsonProcessingException e) {
      log.trace("Unable to get Id, ignoring");
      return null;
    }
  }

  @PreDestroy
  public void shutdown() {
    log.info("Shutting down Producer");
    producer.close();
    wikimediaSubscriber.dispose();
  }
}
