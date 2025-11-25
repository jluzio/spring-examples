package com.example.spring.framework.wiremock;

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
import org.springframework.web.client.RestClient;

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
    stubFor(get("/tests/hello").willReturn(ok(message)));

    RestClient restClient = RestClient.builder()
        .baseUrl(wiremock.baseUrl())
        .build();

    String response = restClient.get().uri("/tests/hello").retrieve().body(String.class);
    log.info(response);
    assertThat(response).isEqualTo(message);

    String messageSimpleResponse = restClient.get().uri("/message/simple").retrieve().body(String.class);
    log.info(messageSimpleResponse);
    assertThat(messageSimpleResponse).isEqualTo("Hello world!");
  }

}
