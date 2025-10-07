package com.example.spring.batch.playground.features.live;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.Future.State;
import java.util.function.Function;
import java.util.stream.IntStream;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.EnabledIf;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.annotation.PostExchange;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@SpringBootTest
@EnabledIf("#{systemEnvironment['LIVE_TEST'] == 'true'}")
@Slf4j
class LiveResilienceTest {

  public static final int MAX_LEN = 100;

  interface ResilientJobApi {

    @PostExchange("/jobs/resilience/resilientJob")
    ResponseEntity<?> startJob(@RequestBody Map<String, Object> params);
  }

  @Builder
  record ResponseData(int id, State state, Object dataRepresentation, @JsonIgnore Object data) {

  }

  @Configuration
  @Import({JacksonAutoConfiguration.class})
  static class Config {

  }


  @Autowired
  ObjectMapper objectMapper;


  @Test
  void test_failure() throws InterruptedException, ExecutionException, IOException {
    runTest(1000, this::paramsLongFailure);
  }

  @Test
  void test_success() throws InterruptedException, ExecutionException, IOException {
    runTest(1000, this::paramsFastSuccess);
  }

  @Test
  void test_quick_success() throws InterruptedException, ExecutionException, IOException {
    runTest(1, this::paramsFastSuccess);
  }

  void runTest(int executions, Function<Integer, Map<String, Object>> paramsSupplier)
      throws InterruptedException, ExecutionException, IOException {
    var api = resilientJobApi();
    var fileObjectMapper = new ObjectMapper().writerWithDefaultPrettyPrinter();

    var callables = IntStream.rangeClosed(1, executions)
        .mapToObj(id -> callApi(id, api, paramsSupplier))
        .toList();

    try (var executorService = Executors.newVirtualThreadPerTaskExecutor()) {
      var futures = executorService.invokeAll(callables);
      var responseDataList = new ArrayList<ResponseData>();
      for (int i = 0; i < futures.size(); i++) {
        var id = i + 1;
        var future = futures.get(i);
        var responseData = getResponseData(future, id);
        responseDataList.add(responseData);
        log.debug("Execution({}) :: {}", id, responseData);
      }

      fileObjectMapper.writeValue(new File("data.json"), responseDataList);
      log.debug("executions:{}{}", System.lineSeparator(), objectMapper.writeValueAsString(responseDataList));
    }
  }

  private ResilientJobApi resilientJobApi() {
    var webClient = RestClient.builder()
        .baseUrl("http://localhost:8080")
        .build();
    var adapter = RestClientAdapter.create(webClient);
    var factory = HttpServiceProxyFactory.builderFor(adapter).build();
    return factory.createClient(ResilientJobApi.class);
  }

  private Callable<ResponseEntity<?>> callApi(int id, ResilientJobApi api,
      Function<Integer, Map<String, Object>> paramsSupplier) {
    var params = paramsSupplier.apply(id);
    return () -> api.startJob(params);
  }

  private Map<String, Object> paramsLongFailure(int id) {
    return Map.of(
        "runId", "live-test-" + id,
        "data", "1,2,3",
        "failuresMap", "1=1,2=0,3=3",
        "backOffPeriod", 30000
    );
  }

  private Map<String, Object> paramsFastSuccess(int id) {
    return Map.of(
        "runId", "live-test-" + id,
        "data", "1,2,3",
        "failuresMap", "1=1,2=0,3=2",
        "backOffPeriod", MAX_LEN
    );
  }

  private ResponseData getResponseData(Future<ResponseEntity<?>> future, int id)
      throws ExecutionException, InterruptedException {
    var state = future.state();
    var responseDataBuilder = ResponseData.builder()
        .id(id)
        .state(state);
    if (state == State.SUCCESS) {
      var data = future.get();
      var bodyTruncated = Optional.ofNullable(data.getBody())
          .map(Object::toString)
          .map(s -> truncateData(s, MAX_LEN))
          .orElse(null);
      return responseDataBuilder
          .data(data)
          .dataRepresentation("statusCode: %s | body: %s".formatted(data.getStatusCode(), bodyTruncated))
          .build();
    } else {
      var data = future.exceptionNow();
      return responseDataBuilder
          .data(data)
          .dataRepresentation(truncateData(data.getMessage(), MAX_LEN))
          .build();
    }
  }

  private String truncateData(String data, int maxLen) {
    return data.length() <= maxLen
        ? data
        : data.substring(0, maxLen) + "...";
  }

}
