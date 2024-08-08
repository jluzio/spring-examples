package com.example.spring.messaging.kafka.spring.base;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import lombok.extern.log4j.Log4j2;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;

@SpringBootTest(properties = {
    "spring.autoconfigure.exclude=org.springframework.cloud.stream.test.binder.TestSupportBinderAutoConfiguration"
})
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
      var results = consumer.poll(Duration.ofMillis(100));
      log.info("consumer results: {}", results.count());
    }
  }

}
