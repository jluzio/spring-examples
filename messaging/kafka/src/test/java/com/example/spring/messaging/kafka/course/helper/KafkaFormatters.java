package com.example.spring.messaging.kafka.course.helper;

import com.google.common.base.MoreObjects;
import lombok.experimental.UtilityClass;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

@UtilityClass
public class KafkaFormatters {

  public String format(RecordMetadata recordMetadata) {
    return MoreObjects.toStringHelper(recordMetadata)
        .add("topic", recordMetadata.topic())
        .add("partition", recordMetadata.partition())
        .add("offset", recordMetadata.offset())
        .add("timestamp", recordMetadata.timestamp())
        .toString();
  }

  public String format(ConsumerRecord<?,?> consumerRecord) {
    return MoreObjects.toStringHelper(consumerRecord)
        .add("key", consumerRecord.key())
        .add("value", consumerRecord.value())
        .add("topic", consumerRecord.topic())
        .add("partition", consumerRecord.partition())
        .add("offset", consumerRecord.offset())
        .add("timestamp", consumerRecord.timestamp())
        .toString();
  }

}
