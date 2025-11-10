package com.example.spring.framework.web.util;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.URI;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.web.util.UriTemplate;

class UriTemplateTest {

  @Test
  void test_complete() {
    UriTemplate template = new UriTemplate("http://server.com/base-path/{resource}?param1={param1}");
    URI uri = template.expand(Map.of(
        "resource", "res1",
        "param1", "qp1"
    ));
    assertThat(uri)
        .hasToString("http://server.com/base-path/res1?param1=qp1");
  }

  @Test
  void test_partial() {
    UriTemplate template = new UriTemplate("/base-path/{resource}?param1={param1}");
    URI uri = template.expand(Map.of(
        "resource", "res1",
        "param1", "qp1"
    ));
    assertThat(uri)
        .hasToString("/base-path/res1?param1=qp1");
  }

}
