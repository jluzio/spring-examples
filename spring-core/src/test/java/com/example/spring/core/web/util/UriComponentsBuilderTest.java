package com.example.spring.core.web.util;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.URI;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.web.util.DefaultUriBuilderFactory;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.web.util.UriTemplate;

/**
 * @see <a href="https://docs.spring.io/spring-framework/reference/web/webmvc/mvc-uri-building.html">URI Links</a>
 */
class UriComponentsBuilderTest {

  @Test
  void example_usage() {
    UriComponents uriComponents = UriComponentsBuilder
        .fromUriString("https://example.com/hotels/{hotel}")
        .queryParam("q", "{q}")
        .encode()
        .build();

    URI uri = uriComponents.expand("Westin", "123").toUri();
    assertThat(uri.toString())
        .isEqualTo("https://example.com/hotels/Westin?q=123");
  }

  @Test
  void example_usage_shortcuts() {
    URI uri1 = UriComponentsBuilder
        .fromUriString("https://example.com/hotels/{hotel}")
        .queryParam("q", "{q}")
        .encode()
        .buildAndExpand("Westin", "123")
        .toUri();
    assertThat(uri1.toString())
        .isEqualTo("https://example.com/hotels/Westin?q=123");

    URI uri2 = UriComponentsBuilder
        .fromUriString("https://example.com/hotels/{hotel}")
        .queryParam("q", "{q}")
        .build("Westin", "123");
    assertThat(uri2.toString())
        .isEqualTo("https://example.com/hotels/Westin?q=123");

    URI uri3 = UriComponentsBuilder
        .fromUriString("https://example.com/hotels/{hotel}?q={q}")
        .build("Westin", "123");
    assertThat(uri3.toString())
        .isEqualTo("https://example.com/hotels/Westin?q=123");
  }

  @Test
  void defaultUriBuilderFactory() {
    String baseUrl = "https://example.com";
    DefaultUriBuilderFactory uriBuilderFactory = new DefaultUriBuilderFactory(baseUrl);

    URI uri = uriBuilderFactory.uriString("/hotels/{hotel}")
        .queryParam("q", "{q}")
        .build("Westin", "123");
    assertThat(uri.toString())
        .isEqualTo("https://example.com/hotels/Westin?q=123");
  }

  @Test
  void simple_tests() {
    UriComponents uriComponents = UriComponentsBuilder
        .fromUriString("http://server.com/base-path/{resource}?param1={param1}")
        .uriVariables(Map.of(
            "resource", "res1",
            "param1", "qp1"
        ))
        .build();
    assertThat(uriComponents.toUriString())
        .isEqualTo("http://server.com/base-path/res1?param1=qp1");

    UriComponents uriComponentsPartial = UriComponentsBuilder
        .fromUriString("/base-path/{resource}?param1={param1}")
        .uriVariables(Map.of(
            "resource", "res1",
            "param1", "qp1"
        ))
        .build();
    assertThat(uriComponentsPartial.toUriString())
        .isEqualTo("/base-path/res1?param1=qp1");
  }

}
