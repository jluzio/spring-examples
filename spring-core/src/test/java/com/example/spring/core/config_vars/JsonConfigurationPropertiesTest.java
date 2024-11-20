package com.example.spring.core.config_vars;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import lombok.Builder;
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
import uk.org.webcompere.systemstubs.environment.EnvironmentVariables;

@Log4j2
class JsonConfigurationPropertiesTest {

  @ConfigurationProperties("app.json-data")
  @Builder
  public record JsonData(String key, String value) {}

  @Configuration
  @EnableConfigurationProperties(JsonData.class)
  static class Config {

    @Bean
    JsonData jsonDataEnv(@Value("${app.json-data}") String json) throws JsonProcessingException {
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

    var jsonData = app.getBean(JsonData.class);
    log.info("data: {}", jsonData);
    assertThat(jsonData).isEqualTo(expectedJsonData);
  }

  @Test
  void test_environment() throws Exception {
    EnvironmentVariables environmentVariables = new EnvironmentVariables(
        "APP_JSON_DATA", objectMapper.writeValueAsString(expectedJsonData));

    environmentVariables.execute(() -> {
      var app = app()
          .run();

      var environment = app.getBean(Environment.class);
      log.info("env: {}", environment.getProperty("app.json-data"));

      var jsonData = app.getBean("jsonDataEnv", JsonData.class);
      log.info("data: {}", jsonData);
      assertThat(jsonData).isEqualTo(new JsonData("key1", "value1"));
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

    var jsonData = app.getBean("jsonDataEnv", JsonData.class);
    log.info("data: {}", jsonData);
    assertThat(jsonData).isEqualTo(new JsonData("key1", "value1"));
  }

  SpringApplicationBuilder app() {
    return new SpringApplicationBuilder(Config.class)
        .web(WebApplicationType.NONE);
  }

}
