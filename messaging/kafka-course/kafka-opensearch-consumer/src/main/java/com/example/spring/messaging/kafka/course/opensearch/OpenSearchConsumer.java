package com.example.spring.messaging.kafka.course.opensearch;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Log4j2
public class OpenSearchConsumer implements ApplicationRunner {

  private final KafkaConfigProps kafkaConfigProps;
  private final OpenSearchService openSearchService;
  private final ObjectMapper objectMapper;
  private KafkaConsumer<String, String> consumer;

  @Override
  public void run(ApplicationArguments args) throws Exception {
    Map<String, Object> kafkaConfig = kafkaConfigProps.kafkaConfig(Map.of(
        ConsumerConfig.GROUP_ID_CONFIG, "myapp",
        ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest"
        // high throughput config
//        ProducerConfig.COMPRESSION_TYPE_CONFIG, "snappy"
    ));

    consumer = new KafkaConsumer<>(kafkaConfig);

    // doesn't get called if class is ran with gradle (requires --no-daemon)
    // switch to running with "IntelliJ Idea" in "Build Tools" > "Gradle" > "Build and run using:"
    var mainThread = Thread.currentThread();
    Runtime.getRuntime().addShutdownHook(new Thread(() -> {
      try {
        log.warn("Closing consumer");
        consumer.wakeup();
        mainThread.join();
      } catch (InterruptedException e) {
        throw new RuntimeException(e);
      }
    }));

    consumer.subscribe(List.of(kafkaConfigProps.getTopic()));

    openSearchService.createIndexIfRequired();

    try {
      while (true) {
        log.info("Polling");
        ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(1000));
        log.info("Processing Wikimedia records: {}", records.count());
        for (var record : records) {
          openSearchService.index(record);
        }
      }
    } catch (WakeupException e) {
      log.info("Interrupted consumer, closing down");
    } catch (Exception e) {
      log.error("Unexpected exception", e);
    } finally {
      // close and commit offsets
      consumer.close();
    }
  }
}
