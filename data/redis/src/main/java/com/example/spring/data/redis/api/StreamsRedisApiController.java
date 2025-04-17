package com.example.spring.data.redis.api;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.stream.Consumer;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.connection.stream.ReadOffset;
import org.springframework.data.redis.connection.stream.StreamOffset;
import org.springframework.data.redis.connection.stream.StreamReadOptions;
import org.springframework.data.redis.connection.stream.StreamRecords;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StreamOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.stream.StreamListener;
import org.springframework.data.redis.stream.StreamMessageListenerContainer;
import org.springframework.data.redis.stream.StreamMessageListenerContainer.StreamMessageListenerContainerOptions;
import org.springframework.data.redis.stream.Subscription;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/redis-api/streams")
@RequiredArgsConstructor
@Slf4j
public class StreamsRedisApiController {

  public static final String MY_STREAM = "my-stream";
  public static final String MY_GROUP = "my-group";
  public static final String MY_CONSUMER = "my-consumer";
  private final StringRedisTemplate redisTemplate;
  private Subscription subscription;
  private StreamMessageListenerContainer<?, ?> streamMessageListenerContainer;

  @PostConstruct
  public void init() {
    RedisConnectionFactory connectionFactory = redisTemplate.getConnectionFactory();
    StreamListener<String, MapRecord<String, String, String>> streamListener = this.streamListener();

    StreamMessageListenerContainerOptions<String, MapRecord<String, String, String>> containerOptions = StreamMessageListenerContainerOptions
        .builder().pollTimeout(Duration.ofMillis(100)).build();

    StreamMessageListenerContainer<String, MapRecord<String, String, String>> container = StreamMessageListenerContainer.create(
        connectionFactory,
        containerOptions);

    Subscription subscription = container.receive(StreamOffset.fromStart(MY_STREAM), streamListener);

    this.subscription = subscription;
    this.streamMessageListenerContainer = container;

    container.start();
  }

  @PreDestroy
  public void destroy() {
    if (subscription != null) {
      subscription.cancel();
    }
    if (streamMessageListenerContainer != null) {
      streamMessageListenerContainer.stop();
    }
  }

  @PutMapping("add")
  public void add(@RequestBody Map<String, String> recordData) {
    var record = StreamRecords.newRecord()
        .ofMap(recordData)
        .withStreamKey(MY_STREAM);
    redisTemplate.opsForStream().add(record);
  }

  @PutMapping("group_create")
  public void groupCreate() {
    redisTemplate.opsForStream().createGroup(MY_STREAM, MY_GROUP);
  }

  @GetMapping("group_read")
  public List<MapRecord<String, String, String>> groupRead() {
    @SuppressWarnings("unchecked")
    List<MapRecord<String, String, String>> messages = redisTemplate.<String, String>opsForStream()
        .read(
            Consumer.from(MY_GROUP, MY_CONSUMER),
            StreamReadOptions.empty().count(2),
            StreamOffset.create(MY_STREAM, ReadOffset.lastConsumed())
        );
    return messages;
  }

  public StreamListener<String, MapRecord<String, String, String>> streamListener() {
    return message -> {
      log.debug("streamListener :: message: {}", message);
    };
  }

}
