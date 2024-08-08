package com.example.spring.messaging.kafka.core.course.basics.runner;

import com.example.spring.messaging.kafka.core.course.helper.KafkaFormatters;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

class ConsumerBasicDemo extends BasicDemoRunner {

  public static void main(String[] args) {
    runApp(ConsumerBasicDemo.class, ConsumerBasicDemo::run);
  }

  public void run() {
    var kafkaConfig = config.kafkaConfig(Map.of(
        ConsumerConfig.GROUP_ID_CONFIG, "myapp",
        ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest"
    ));
    var consumer = new KafkaConsumer<String, String>(kafkaConfig);

    consumer.subscribe(List.of(config.defaultTopic()));

    ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(200));
    for (var record : records) {
      log.info("record: {}", KafkaFormatters.format(record));
    }

    // close and commit offsets
    consumer.close();
  }

}
