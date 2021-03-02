package com.example.spring.core.wiremock;

import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static org.assertj.core.api.Assertions.assertThat;

import de.mkammerer.wiremock.WireMockExtension;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.reactive.function.client.WebClient;

@SpringBootTest
@Slf4j
class WireMockWithExtensionTest {

  @RegisterExtension
  WireMockExtension wireMock = new WireMockExtension(8888);

  @Test
  void test() {
    int wiremockServerPort = wireMock.getOptions().portNumber();

    String message = "World!";
    wireMock.stubFor(get("/hello").willReturn(ok(message)));

    WebClient webClient = WebClient.builder()
//        .baseUrl("http://localhost:%s/".formatted(wiremockServerPort))
        .baseUrl(wireMock.getBaseUri().resolve("/").toString())
        .build();

    String response = webClient.get().uri("/hello")
        .exchangeToMono(clientResponse -> clientResponse.bodyToMono(String.class))
        .block();
    log.info(response);
    assertThat(response).isEqualTo(message);
  }

}
