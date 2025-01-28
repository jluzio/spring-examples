package com.example.spring.core.profile;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;
import java.util.Map;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles({"props-merge-base", "props-merge-override"})
@Log4j2
class ProfilePropertiesMergeTest {

  @Configuration
  @EnableConfigurationProperties
  static class Config {

    @Bean
    @ConfigurationProperties("app.some-map-bean")
    Map<String, String> someMapBean() {
      return new HashMap<>();
    }
  }

  @Autowired
  Environment environment;
  @Autowired
  Map<String, String> someMapBean;

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

}
