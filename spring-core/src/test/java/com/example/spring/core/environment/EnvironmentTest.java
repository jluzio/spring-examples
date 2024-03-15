package com.example.spring.core.environment;

import static java.util.Optional.ofNullable;
import static org.assertj.core.api.Assertions.assertThat;

import com.google.common.collect.ImmutableMap;
import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@Slf4j
@ActiveProfiles("env_testing")
class EnvironmentTest {

  @Autowired
  Environment env;

  @Test
  void test() {
    assertThat(env).isNotNull();

    log.info("environment info :: profiles={}", (Object) env.getActiveProfiles());

    Map<String, Function<String, Object>> mappings = ImmutableMap.of(
        "logging.level.com.example.spring", env::getProperty,
        "app.test.string", env::getProperty,
        "app.test.csv",
        key -> ofNullable(env.getProperty(key, Integer[].class)).map(Arrays::asList),
        "app.test.composite", env::getProperty,
        "app.test.composite-quoted", env::getProperty
    );
    mappings.entrySet().stream()
        .forEach(entry -> {
          log.info("{} = {}", entry.getKey(), entry.getValue().apply(entry.getKey()));
        });
  }

  @Configuration
  @PropertySources({
      @PropertySource("classpath:/test.properties")
  })
  static class Config {

  }

}
