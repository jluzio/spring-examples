package com.example.spring.messaging.kafka.spring;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import lombok.extern.log4j.Log4j2;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("kafka-apps-disabled")
@EmbeddedKafka(kraft = true, topics = SpringContextKafkaTest.TOPIC)
@Log4j2
class SpringContextKafkaTest {

  public static final String TOPIC = "test-topic";

  @Autowired
  KafkaTemplate<String, String> template;
  @Autowired
  EmbeddedKafkaBroker broker;


  @Test
  void test() {
    template.send(TOPIC, "123");

    var consumerProps = KafkaTestUtils.consumerProps("test-group", "true", broker);
    consumerProps.putAll(Map.of(
        ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName(),
        ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName()
    ));
    try (var consumer = new KafkaConsumer<String, String>(consumerProps)) {
      consumer.subscribe(List.of(TOPIC));
      var results = KafkaTestUtils.getRecords(consumer);

      log.info("consumer results: {}", results.count());
      results.forEach(log::info);

      assertThat(results.count())
          .isOne();
    }
  }

}
