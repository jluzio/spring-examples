package com.example.spring.framework.wiremock;

import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.assertj.core.api.Assertions.assertThat;

import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.client.RestClient;

@SpringBootTest
@Slf4j
class WireMockWithExtensionTest {

  @RegisterExtension
  static WireMockExtension wireMock = WireMockExtension.newInstance()
      .options(wireMockConfig().port(8888))
      .build();
  @Test
  void test() {
    int wiremockServerPort = wireMock.getOptions().portNumber();
    log.debug("{}", wiremockServerPort);

    String message = "World!";
    wireMock.stubFor(get("/hello").willReturn(ok(message)));

    RestClient restClient = RestClient.builder()
        .baseUrl(wireMock.getRuntimeInfo().getHttpBaseUrl())
        .build();

    String response = restClient.get().uri("/hello").retrieve().body(String.class);
    log.info(response);
    assertThat(response).isEqualTo(message);
  }

}
