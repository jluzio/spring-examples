package com.example.spring.messaging.kafka.core.course.wikimedia;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/wikimedia")
public class WikimediaController {

  private final WebClient webClient = WebClient.create("https://stream.wikimedia.org/v2");

  @GetMapping("/recentchange")
  public Flux<ServerSentEvent<String>> recentChange() {
    return webClient.get()
        .uri("/stream/recentchange")
        .retrieve()
        .bodyToFlux(new ParameterizedTypeReference<>() {
        });
  }

}
