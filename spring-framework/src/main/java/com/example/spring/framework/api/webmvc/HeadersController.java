package com.example.spring.framework.api.webmvc;

import java.util.Map;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class HeadersController {

  @GetMapping(path = "/debug/headers")
  public String headers(
      @RequestHeader HttpHeaders httpHeaders,
      @RequestHeader Map<String, String> httpHeadersAsMap,
      @RequestHeader MultiValueMap<String, String> httpHeadersAsMultiValueMap
  ) {
    log.info("httpHeaders: {}", httpHeaders);
    log.info("httpHeadersAsMap: {}", httpHeadersAsMap);
    log.info("httpHeadersAsMultiValueMap: {}", httpHeadersAsMultiValueMap);
    Stream.of("foo", "FOO").forEach(v -> {
      log.info("httpHeaders.{}: {}", v, httpHeaders.get(v));
      log.info("httpHeadersAsMap.{}: {}", v, httpHeadersAsMap.get(v));
      log.info("httpHeadersAsMultiValueMap.{}: {}", v, httpHeadersAsMultiValueMap.get(v));
    });
    return httpHeaders.toString();
  }

}
