package com.example.spring.messaging.kafka.course.basics.runner;

import com.example.spring.messaging.kafka.course.helper.KafkaFormatters;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.CooperativeStickyAssignor;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;

class ConsumerCooperativeAssignerDemo extends BasicDemoRunner {

  public static void main(String[] args) {
    runApp(ConsumerCooperativeAssignerDemo.class, ConsumerCooperativeAssignerDemo::run);
  }

  public void run() {
    var kafkaConfig = config.kafkaConfig(Map.of(
        ConsumerConfig.GROUP_ID_CONFIG, "myapp",
        ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest",
        // partitions are not all revoked when adding a consumer to the group
        // instead, specific ones are removed or added without a "stop-the-world" event
        ConsumerConfig.PARTITION_ASSIGNMENT_STRATEGY_CONFIG,
        CooperativeStickyAssignor.class.getName()
    ));
    var consumer = new KafkaConsumer<String, String>(kafkaConfig);

    //
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

    consumer.subscribe(List.of(config.defaultTopic()));

    // doesn't work if class is ran with gradle (requires --no-daemon)
    // switch to running with "IntelliJ Idea" in "Build Tools" > "Gradle" > "Build and run using:"
    try {
      while (true) {
        log.info("Polling");
        ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(1000));
        for (var record : records) {
          log.info("record: {}", KafkaFormatters.format(record));
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
