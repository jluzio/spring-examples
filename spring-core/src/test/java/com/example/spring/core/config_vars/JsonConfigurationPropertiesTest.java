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
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.ConfigDataApplicationContextInitializer;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

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
    appCtxRunner()
        .withPropertyValues("spring.profiles.active=json-config-props")
        .run(ctx -> {
          var environment = ctx.getBean(Environment.class);
          log.info("env: {}", environment.getProperty("app.json-data"));

          var jsonDataDefault = ctx.getBean("jsonDataDefault", JsonData.class);
          var jsonDataEnv = ctx.getBean("jsonDataEnv", JsonData.class);
          log.info("jsonDataDefault: {} | jsonDataEnv: {}", jsonDataDefault, jsonDataEnv);
          assertThat(jsonDataDefault).isEqualTo(expectedJsonData);
          assertThat(jsonDataEnv).isNotEqualTo(expectedJsonData);
        });
  }

  @Test
  void test_environment() throws Exception {
    withEnvironmentVariable("APP_JSON_DATA", objectMapper.writeValueAsString(expectedJsonData))
        .execute(() -> {
          appCtxRunner()
              .run(ctx -> {
                var environment = ctx.getBean(Environment.class);
                log.info("env: {}", environment.getProperty("app.json-data"));

                var jsonDataDefault = ctx.getBean("jsonDataDefault", JsonData.class);
                var jsonDataEnv = ctx.getBean("jsonDataEnv", JsonData.class);
                log.info("jsonDataDefault: {} | jsonDataEnv: {}", jsonDataDefault, jsonDataEnv);
                assertThat(jsonDataDefault).isNotEqualTo(expectedJsonData);
                assertThat(jsonDataEnv).isEqualTo(expectedJsonData);
              });
        });
  }

  @Test
  void test_mock_environment_properties() throws JsonProcessingException {
    appCtxRunner()
        .withSystemProperties("app.json-data=%s".formatted(objectMapper.writeValueAsString(expectedJsonData)))
        .run(ctx -> {
          var environment = ctx.getBean(Environment.class);
          log.info("env: {}", environment.getProperty("app.json-data"));

          var jsonDataDefault = ctx.getBean("jsonDataDefault", JsonData.class);
          var jsonDataEnv = ctx.getBean("jsonDataEnv", JsonData.class);
          log.info("jsonDataDefault: {} | jsonDataEnv: {}", jsonDataDefault, jsonDataEnv);
          assertThat(jsonDataDefault).isNotEqualTo(expectedJsonData);
          assertThat(jsonDataEnv).isEqualTo(expectedJsonData);
        });
  }

  ApplicationContextRunner appCtxRunner() {
    return new ApplicationContextRunner()
        .withUserConfiguration(Config.class)
        .withInitializer(new ConfigDataApplicationContextInitializer());
  }
}
