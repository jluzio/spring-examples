package com.example.spring.core.log.logback;

import static java.util.Optional.ofNullable;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.core.CoreConstants;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.slf4j.ILoggerFactory;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@SpringBootTest(
    classes = MaskConverterBeanLogTest.Config.class,
    properties = "logging.pattern.console=%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %mask(%m){}%n"
)
@EnableConfigurationProperties
@Slf4j
class MaskConverterBeanLogTest {

  @Configuration
  @Import({MaskConverterConfig.class})
  static class Config {

    @PostConstruct
    void checkInit() {
      System.out.println(System.getProperties());
      ILoggerFactory loggerFactory = LoggerFactory.getILoggerFactory();
      LoggerContext loggerContext = (LoggerContext) loggerFactory;
      Map<String, String> ruleRegistry = ofNullable(
          (Map<String, String>) loggerContext.getObject(CoreConstants.PATTERN_RULE_REGISTRY))
          .orElseGet(HashMap::new);
      System.out.println(ruleRegistry);
//      ruleRegistry.put("mask", MaskConverterProxy.class.getName());
//      ruleRegistry.put("mask", ColorConverter.class.getName());
      System.out.println(ruleRegistry);
      System.out.println();
    }

    @Autowired
    void initMaskConverter(MaskConverter maskConverter) {
      MaskConverterProxy.setConverter(maskConverter);
    }
  }

  @Autowired
  MaskConverter converter;

  @Test
  void test() {
    log.warn("test()");
    log.warn("1234 1234561234561234");
    System.out.println();
  }
}