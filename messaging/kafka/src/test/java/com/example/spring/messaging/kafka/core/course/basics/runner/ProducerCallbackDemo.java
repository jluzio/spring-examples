package com.example.spring.messaging.kafka.core.course.basics.runner;

import com.example.spring.messaging.kafka.core.course.helper.KafkaFormatters;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.assertj.core.api.Assertions;

class ProducerCallbackDemo extends BasicDemoRunner {

  public static void main(String[] args) {
    runApp(ProducerCallbackDemo.class, ProducerCallbackDemo::run);
  }

  public void run() {
    var producer = new KafkaProducer<String, String>(config.basicKafkaConfig());

    List<RecordMetadata> recordMetadataList = new ArrayList<>();
    IntStream.rangeClosed(1, 10).forEach(i -> {
      var producerRecord = new ProducerRecord<String, String>(config.defaultTopic(), "val=" + i);

      producer.send(producerRecord, (metadata, exception) -> {
        log.info("metadata: {} | exception: {}", KafkaFormatters.format(metadata), exception);
        recordMetadataList.add(metadata);
      });
    });

    // sync wait for completion of send operation
    producer.flush();

    var partitions = recordMetadataList.stream()
        .map(RecordMetadata::partition)
        .collect(Collectors.toSet());
    // Sticky partition
    // all messages were sent in a batch to one partition, since key=null and batch size allows it
    Assertions.assertThat(partitions)
        .hasSize(1);

    // close also flushes
    producer.close();
  }

}
