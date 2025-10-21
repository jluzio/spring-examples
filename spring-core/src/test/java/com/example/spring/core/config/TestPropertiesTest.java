package com.example.spring.core.config;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest(properties = {
    "test.p1=v1",
    "test.p2:v2",
})
@TestPropertySource(properties = {
    "test.p3=v3",
    "test.p4:v4",
})
class TestPropertiesTest {

  @Configuration
  static class Config {

  }

  @Value("${test.p1:default}")
  String p1;
  @Value("${test.p2:default}")
  String p2;
  @Value("${test.p3:default}")
  String p3;
  @Value("${test.p4:default}")
  String p4;

  @Test
  void properties_variations() {
    assertThat(p1).isEqualTo("v1");
    assertThat(p2).isEqualTo("v2");
    assertThat(p3).isEqualTo("v3");
    assertThat(p4).isEqualTo("v4");
  }
}
