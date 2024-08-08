package com.example.spring.messaging.kafka.spring.base;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import lombok.extern.log4j.Log4j2;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.Test;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;

@EmbeddedKafka(kraft = true, topics = NoSpringContextKafkaTest.TOPIC)
@Log4j2
class NoSpringContextKafkaTest {

  public static final String TOPIC = "test-topic";

  @Test
  void test(EmbeddedKafkaBroker broker) {
    var producerProps = KafkaTestUtils.producerProps(broker);
    producerProps.putAll(Map.of(
        ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName(),
        ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName()
    ));
    try (var producer = new KafkaProducer<String, String>(producerProps)) {
      producer.send(new ProducerRecord<>(TOPIC, "123"));
    }

    var consumerProps = KafkaTestUtils.consumerProps("test-group", "true", broker);
    consumerProps.putAll(Map.of(
        ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName(),
        ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName()
    ));
    try (var consumer = new KafkaConsumer<String, String>(consumerProps)) {
      consumer.subscribe(List.of(TOPIC));
      var results = consumer.poll(Duration.ofMillis(100));
      log.info("consumer results: {}", results.count());
    }
  }

}
