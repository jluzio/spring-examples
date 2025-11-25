package com.example.spring.framework.api;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.spring.framework.api.MockRestServiceServerTest.Config.RemoteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.restclient.test.autoconfigure.RestClientTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.test.web.client.ExpectedCount;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.client.match.MockRestRequestMatchers;
import org.springframework.test.web.client.response.MockRestResponseCreators;
import org.springframework.web.client.RestClient;

// included: @AutoConfigureMockRestServiceServer
@RestClientTest
@Log4j2
class MockRestServiceServerTest {

  public static final String PING_ENDPOINT = "https://some-svc.server.com/api/ping";

  @TestConfiguration
  static class Config {

    @Bean
    RestClient restClient(RestClient.Builder builder) {
      return builder
          .build();
    }

    @Component
    @RequiredArgsConstructor
    public static class RemoteService {

      private final RestClient restClient;

      public String ping() {
        return restClient.get()
            .uri(PING_ENDPOINT)
            .retrieve()
            .body(String.class);
      }
    }
  }

  @Autowired
  RestClient restClient;
  @Autowired
  RemoteService remoteService;
  @Autowired
  MockRestServiceServer mockRestServiceServer;

  @Test
  void test_ok() {
    mockRestServiceServer.expect(ExpectedCount.twice(), MockRestRequestMatchers.requestTo(PING_ENDPOINT))
        .andRespond(MockRestResponseCreators.withSuccess("pong", MediaType.TEXT_PLAIN));

    var responseBody = restClient
        .get().uri(PING_ENDPOINT)
        .retrieve()
        .body(String.class);
    assertThat(responseBody)
        .isNotNull()
        .satisfies(log::debug)
        .isEqualTo("pong");

    var serviceResponseBody = remoteService.ping();
    assertThat(serviceResponseBody)
        .isNotNull()
        .satisfies(log::debug)
        .isEqualTo("pong");

    mockRestServiceServer.verify();
  }

}
