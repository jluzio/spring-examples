package com.example.spring.framework.jackson;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = {JacksonAutoConfiguration.class})
class JacksonMixinTest {

  // @formatter:off
  interface Event {}
  record FooEvent (String text) implements Event {}
  record BarEvent (String text) implements Event {}
  @JsonTypeInfo(use = Id.CLASS, property = "@type")
  final class EventMixIn {}
  // @formatter:on

  @Autowired
  ObjectMapper objectMapper;

  @Test
  void defaultUsage() throws JsonProcessingException {
    var fooJson = objectMapper.writeValueAsString(new FooEvent("somevalue"));
    assertThat(fooJson).isEqualTo("""
        {"text":"somevalue"}""");

    var barJson = objectMapper.writeValueAsString(new BarEvent("somevalue"));
    assertThat(barJson).isEqualTo("""
        {"text":"somevalue"}""");

    assertThat(fooJson)
        .isEqualTo(barJson);
  }

  @Test
  void mixin() throws JsonProcessingException {
    var objectMapperMixin = JsonMapper.builder()
        .addMixIn(Event.class, EventMixIn.class)
        .build();

    var fooJson = objectMapperMixin.writeValueAsString(new FooEvent("somevalue"));
    assertThat(fooJson).isEqualTo("""
        {"@type":"%s","text":"somevalue"}""".formatted(FooEvent.class.getName()));

    var barJson = objectMapperMixin.writeValueAsString(new BarEvent("somevalue"));
    assertThat(barJson).isEqualTo("""
        {"@type":"%s","text":"somevalue"}""".formatted(BarEvent.class.getName()));

    assertThat(fooJson)
        .isNotEqualTo(barJson);
  }
}
