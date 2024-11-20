package com.example.spring.core.config_vars;

import static org.assertj.core.api.Assertions.assertThat;
import static uk.org.webcompere.systemstubs.SystemStubs.withEnvironmentVariable;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.mock.env.MockEnvironment;

@Log4j2
class JsonConfigurationPropertiesTest {

  @Data
  @RequiredArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class JsonData {

    private String key;
    private String value;
  }

  @Configuration(proxyBeanMethods = false)
  @EnableConfigurationProperties
  static class Config {

    @Bean
    @ConfigurationProperties("app.json-data")
    JsonData jsonDataDefault() {
      return new JsonData();
    }

    @Bean
    JsonData jsonDataEnv(@Value("${app.json-data:{}}") String json) throws JsonProcessingException {
      ObjectMapper objectMapper = JsonMapper.builder().build();
      return objectMapper.readValue(json, JsonData.class);
    }

  }

  JsonData expectedJsonData = new JsonData("key1", "value1");
  ObjectMapper objectMapper = JsonMapper.builder().build();

  @Test
  void test_application_properties() {
    var app = app()
        .profiles("json-config-props")
        .run();

    var environment = app.getBean(Environment.class);
    log.info("env: {}", environment.getProperty("app.json-data"));

    var jsonDataDefault = app.getBean("jsonDataDefault", JsonData.class);
    var jsonDataEnv = app.getBean("jsonDataEnv", JsonData.class);
    log.info("jsonDataDefault: {} | jsonDataEnv: {}", jsonDataDefault, jsonDataEnv);
    assertThat(jsonDataDefault).isEqualTo(expectedJsonData);
    assertThat(jsonDataEnv).isNotEqualTo(expectedJsonData);
  }

  @Test
  void test_environment() throws Exception {
    withEnvironmentVariable("APP_JSON_DATA", objectMapper.writeValueAsString(expectedJsonData))
        .execute(() -> {
          var app = app()
              .run();

          var environment = app.getBean(Environment.class);
          log.info("env: {}", environment.getProperty("app.json-data"));

          var jsonDataDefault = app.getBean("jsonDataDefault", JsonData.class);
          var jsonDataEnv = app.getBean("jsonDataEnv", JsonData.class);
          log.info("jsonDataDefault: {} | jsonDataEnv: {}", jsonDataDefault, jsonDataEnv);
          assertThat(jsonDataDefault).isNotEqualTo(expectedJsonData);
          assertThat(jsonDataEnv).isEqualTo(expectedJsonData);
        });
  }

  @Test
  void test_mock_environment_properties() throws JsonProcessingException {
    var app = app()
        .environment(new MockEnvironment()
            .withProperty("app.json-data", objectMapper.writeValueAsString(expectedJsonData)))
        .run();

    var environment = app.getBean(Environment.class);
    log.info("env: {}", environment.getProperty("app.json-data"));

    var jsonDataDefault = app.getBean("jsonDataDefault", JsonData.class);
    var jsonDataEnv = app.getBean("jsonDataEnv", JsonData.class);
    log.info("jsonDataDefault: {} | jsonDataEnv: {}", jsonDataDefault, jsonDataEnv);
    assertThat(jsonDataDefault).isNotEqualTo(expectedJsonData);
    assertThat(jsonDataEnv).isEqualTo(expectedJsonData);
  }

  SpringApplicationBuilder app() {
    return new SpringApplicationBuilder(Config.class)
        .web(WebApplicationType.NONE);
  }
}
