package com.example.spring.messaging.kafka.course.wikimedia;

import com.example.spring.messaging.kafka.test.TestSupport;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.test.context.junit.jupiter.EnabledIf;
import org.springframework.web.reactive.function.client.WebClient;

@SpringBootTest
@EnabledIf(TestSupport.LIVE_TEST)
@Log4j2
class WikimediaClientTest {

  @Configuration
  static class Config {

  }

  @Test
  void test() {
    WebClient webClient = WebClient.create();
    webClient.get()
        .uri("https://stream.wikimedia.org/v2/stream/recentchange")
        .retrieve()
        .bodyToFlux(new ParameterizedTypeReference<ServerSentEvent<String>>() {
        })
        .take(10)
        .doOnNext(event -> {
          log.debug("event: {}", event);
        })
        .blockLast();
  }

}