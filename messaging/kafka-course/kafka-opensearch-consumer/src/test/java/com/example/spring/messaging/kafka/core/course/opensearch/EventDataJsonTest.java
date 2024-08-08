package com.example.spring.messaging.kafka.core.course.opensearch;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.stream.Stream;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;

@SpringBootTest(classes = {JacksonAutoConfiguration.class})
@Log4j2
class EventDataJsonTest {


  @Value("classpath:data/event1.json")
  Resource event1Resource;
  @Value("classpath:data/event2.json")
  Resource event2Resource;
  @Value("classpath:data/event3.json")
  Resource event3Resource;
  @Autowired
  ObjectMapper objectMapper;

  @Test
  void test_id_retrieval() {
    Stream.of(event1Resource, event2Resource, event3Resource).forEach(eventResource -> {
      try {
        var headerSeparator = Strings.repeat("-", 40);
        log.debug("{} {} {}", headerSeparator, eventResource, headerSeparator);
        var eventJsonNode = objectMapper.readTree(eventResource.getURL());
        log.debug(eventJsonNode.path("meta").path("id"));
        log.debug(eventJsonNode.at("/meta/id"));
        log.debug(eventJsonNode.at("/meta/id").asText());

        assertThat(eventJsonNode.at("/meta/id").asText())
            .isNotEmpty();
      } catch (IOException e) {
        throw new UncheckedIOException(e);
      }
    });
  }

}
