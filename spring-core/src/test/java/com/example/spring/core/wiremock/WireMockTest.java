package com.example.spring.core.wiremock;

import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static org.assertj.core.api.Assertions.assertThat;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.maciejwalkowiak.wiremock.spring.ConfigureWireMock;
import com.maciejwalkowiak.wiremock.spring.EnableWireMock;
import com.maciejwalkowiak.wiremock.spring.InjectWireMock;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.web.reactive.function.client.WebClient;

@SpringBootTest(webEnvironment = WebEnvironment.NONE)
@EnableWireMock({
    @ConfigureWireMock(name = "default", stubLocation = ".")
})
@Slf4j
class WireMockTest {

  @InjectWireMock("default")
  WireMockServer wiremock;

  @Test
  void test() {
    // See Also: wiremock-playground project

    WireMock.configureFor(wiremock.port());

    String message = "World!";
    stubFor(get("/hello").willReturn(ok(message)));

    WebClient webClient = WebClient.builder()
        .baseUrl(wiremock.baseUrl())
        .build();

    String response = webClient.get().uri("/hello")
        .exchangeToMono(clientResponse -> clientResponse.bodyToMono(String.class))
        .block();
    log.info(response);
    assertThat(response).isEqualTo(message);

    String messageSimpleResponse = webClient.get().uri("/message/simple")
        .exchangeToMono(clientResponse -> clientResponse.bodyToMono(String.class))
        .block();
    log.info(messageSimpleResponse);
    assertThat(messageSimpleResponse).isEqualTo("Hello world!");
  }

}
