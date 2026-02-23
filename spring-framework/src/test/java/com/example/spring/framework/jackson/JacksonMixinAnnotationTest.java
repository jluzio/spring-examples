package com.example.spring.framework.jackson;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.jackson.JacksonMixin;
import org.springframework.boot.jackson.autoconfigure.JacksonAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;

@SpringBootTest
class JacksonMixinAnnotationTest {

  // @formatter:off
  interface Event {}
  record FooEvent (String text) implements Event {}
  record BarEvent (String text) implements Event {}
  @JacksonMixin(Event.class)
  @JsonTypeInfo(use = Id.CLASS, property = "@type")
  class EventMixIn {}
  // @formatter:on

  @Configuration
  @Import({JacksonAutoConfiguration.class})
  // only with EnableAutoConfiguration the base package is picked and the MixIn's are detected
  @EnableAutoConfiguration
  static class Config {

  }

  @Autowired
  ObjectMapper objectMapper;

  @Test
  void mixin() {
    var fooJson = objectMapper.writeValueAsString(new FooEvent("somevalue"));
    assertThat(fooJson).isEqualTo("""
        {"@type":"%s","text":"somevalue"}""".formatted(FooEvent.class.getName()));

    var barJson = objectMapper.writeValueAsString(new BarEvent("somevalue"));
    assertThat(barJson).isEqualTo("""
        {"@type":"%s","text":"somevalue"}""".formatted(BarEvent.class.getName()));

    assertThat(fooJson)
        .isNotEqualTo(barJson);
  }
}
