package com.example.spring.core.yaml;

import com.example.spring.core.yaml.YamlTest.Config;
import java.util.Map;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(classes = Config.class)
@Slf4j
@ActiveProfiles("test")
public class YamlTest {

  @Autowired
  ConfigBean configBean;

  @Configuration
  @EnableConfigurationProperties
  @Import(ConfigBean.class)
  static class Config {

  }

  @Data
  public static class PetData {

    private String name;
    private String type;
  }

  @Configuration
  @ConfigurationProperties(prefix = "yaml.config")
  @Data
  static class ConfigBean {

    private String xpto;
    private String asd;
    private Map<String, PetData> petDataMap;

  }

  @Test
  void test() {
    log.info("{}", configBean);
  }
}
