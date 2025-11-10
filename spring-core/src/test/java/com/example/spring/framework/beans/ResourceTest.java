package com.example.spring.framework.beans;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

@SpringBootTest
class ResourceTest {

  @Configuration
  static class Config {

  }

  @Value("classpath:data/text_data.txt")
  Resource textDataResource;
  @Value("classpath:data/doesnt_exist.txt")
  Resource nonExistingDataResource;

  @Test
  void autowire() {
    assertThat(textDataResource.exists())
        .isTrue();
    assertThat(nonExistingDataResource.exists())
        .isFalse();
  }

}
