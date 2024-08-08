package com.example.spring.messaging.kafka.core.course.basics.test;

import java.util.Map;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.Test;

class ProducerTest extends BaseTest {

  @Test
  void basics() {
    Map<String, Object> properties = Map.of(
        CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers,
        ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName(),
        ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName()
    );
    var producer = new KafkaProducer<String, String>(properties);

    var producerRecord = new ProducerRecord<String, String>("first_topic", "some_value");

    producer.send(producerRecord);

    // sync wait for completion of send operation
    producer.flush();

    // close also flushes
    producer.close();
  }

}
