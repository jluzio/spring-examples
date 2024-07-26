package com.example.spring.messaging.kafka.course.basics.runner;

import com.example.spring.messaging.kafka.course.helper.KafkaFormatters;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.assertj.core.api.Assertions;
import reactor.core.publisher.Mono;

class ProducerKeyDemo extends BasicDemoRunner {

  public static void main(String[] args) {
    runApp(ProducerKeyDemo.class, ProducerKeyDemo::run);
  }

  public void run() {
    System.out.println("s");
    var producer = new KafkaProducer<String, String>(config.basicKafkaConfig());

    List<RecordMetadata> recordMetadataList = new ArrayList<>();
    IntStream.rangeClosed(1, 3).forEach(b -> {
      log.info("batch: {}", b);
      IntStream.rangeClosed(1, 10).forEach(i -> {
        var producerRecord = new ProducerRecord<>(config.defaultTopic(), "id=" + i, "val=" + i);

        producer.send(producerRecord, (metadata, exception) -> {
          log.info("key: {} | metadata: {} | exception: {}",
              producerRecord.key(), KafkaFormatters.format(metadata), exception);
          recordMetadataList.add(metadata);
        });
      });
      Mono.delay(Duration.ofMillis(50)).block();
    });

    // sync wait for completion of send operation
    producer.flush();

    // a specific key goes into the partition every time
    var partitions = recordMetadataList.stream()
        .map(RecordMetadata::partition)
        .collect(Collectors.toSet());
    Assertions.assertThat(partitions)
        .hasSize(3);

    // close also flushes
    producer.close();
  }

}
