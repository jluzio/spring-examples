package com.example.spring.framework.profile;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.task.TaskExecutor;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles({"props-merge-base", "props-merge-override"})
@Log4j2
class ProfilePropertiesMergeTest {

  @Data
  @RequiredArgsConstructor
  @AllArgsConstructor
  @Builder
  static class KeyValue {

    private String key;
    private String value;
  }

  @Configuration
  @EnableConfigurationProperties
  static class Config {

    @Bean
    @ConfigurationProperties("app.some-map-bean")
    Map<String, String> someMapBean() {
      return new HashMap<>();
    }

    @Bean
    @ConfigurationProperties("app.some-data-bean")
    KeyValue someDataBean() {
      return new KeyValue();
    }

    @Bean
    @ConfigurationProperties("app.some-list-map-bean")
    List<Map<String, String>> someListMapBean() {
      return new ArrayList<>();
    }

    @Bean
    @ConfigurationProperties("app.some-list-data-bean")
    List<KeyValue> someListDataBean() {
      return new ArrayList<>();
    }
  }

  @Autowired
  Environment environment;
  @Autowired
  Map<String, String> someMapBean;
  @Autowired
  KeyValue someDataBean;
  @Autowired
  List<Map<String, String>> someListMapBean;
  @Autowired
  List<KeyValue> someListDataBean;

  @Autowired(required = false)
  ObjectMapper objectMapper;
  @Autowired(required = false)
  TaskExecutor taskExecutor;
  @Value("${spring.autoconfigure.exclude:}")
  String springAutoconfigureExclude;

  @Test
  void test_autoconfigure() {
    log.debug(springAutoconfigureExclude);
    assertThat(springAutoconfigureExclude)
        .isNotEmpty();

    assertThat(objectMapper)
        .isNull();
    assertThat(taskExecutor)
        .isNull();
  }

  @Test
  void test_map_merge() {
    log.info(someMapBean);
    assertThat(someMapBean)
        .isEqualTo(Map.of(
            "key1", "val1",
            "key2", "val2.2",
            "key3", "val3"
        ));
  }

  @Test
  void test_data_merge() {
    log.info(someDataBean);
    assertThat(someDataBean)
        .isEqualTo(new KeyValue("key1", "val1.2"));
  }

  @Test
  void test_list_map_merge() {
    log.info(someListMapBean);
    assertThat(someListMapBean)
        .isEqualTo(List.of(
                Map.of("key2", "val2.2"),
                Map.of("key3", "val3")
            )
        );
  }

  @Test
  void test_list_data_merge() {
    log.info(someListDataBean);
    assertThat(someListDataBean)
        .isEqualTo(List.of(
                new KeyValue("key2", "val2.2"),
                new KeyValue("key3", "val3")
            )
        );
  }

}
