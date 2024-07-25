package com.example.spring.messaging.kafka.course.basics.runner;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

class ProducerBasicDemo extends BasicDemoRunner {

  public static void main(String[] args) {
    runApp(ProducerBasicDemo.class, ProducerBasicDemo::run);
  }

  public void run() {
    var producer = new KafkaProducer<String, String>(config.basicKafkaConfig());

    var producerRecord = new ProducerRecord<String, String>(config.defaultTopic(), "some_value");

    producer.send(producerRecord);

    // sync wait for completion of send operation
    producer.flush();

    // close also flushes
    producer.close();
  }

}
