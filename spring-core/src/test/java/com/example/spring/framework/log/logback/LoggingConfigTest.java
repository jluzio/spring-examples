package com.example.spring.framework.log.logback;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.core.CoreConstants;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.slf4j.ILoggerFactory;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

@SpringBootTest(
    classes = LoggingConfig.class,
    // Using CONSOLE_LOG_PATTERN/FILE_LOG_PATTERN from logback.xml
    // Test changes with logging.pattern.console property here if necessary
    properties = {
        "tmp.logging.pattern.console=%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %mask(%m){}%n",
        "logging.config=classpath:logback-spring-mask-converter.xml"
    }
)
@EnableConfigurationProperties
@Slf4j
class LoggingConfigTest {

  @MockitoSpyBean
  MaskConverter maskConverter;

  @Test
  void maskConverter() {
    log.info("maskConverter()");
    log.info("Number: 1234561234561234");
    verify(maskConverter, atLeastOnce()).transform(any(), any());
  }

  //  @Test
  void checkInit() {
    System.out.println(System.getProperties());
    ILoggerFactory loggerFactory = LoggerFactory.getILoggerFactory();
    LoggerContext loggerContext = (LoggerContext) loggerFactory;
    Map<String, String> ruleRegistry = (Map<String, String>) loggerContext.getObject(
        CoreConstants.PATTERN_RULE_REGISTRY);
    log.info("ruleRegistry: {}", ruleRegistry);
  }

}