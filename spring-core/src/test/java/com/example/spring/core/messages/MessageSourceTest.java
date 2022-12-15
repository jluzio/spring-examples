package com.example.spring.core.messages;

import java.util.Locale;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.context.MessageSourceAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;

@SpringBootTest
@Slf4j
class MessageSourceTest {

  @Autowired
  MessageSource messageSource;
  @Resource
  MessageSource validationMessageSource;

  @Configuration
  @Import(MessageSourceAutoConfiguration.class)
  static class Config {

    @Bean
    MessageSource validationMessageSource() {
      var messageSource = new ReloadableResourceBundleMessageSource();
      messageSource.setBasename("validation");
      return messageSource;
    }
  }

  @Test
  void test() {
    log.info("hello default: {}", messageSource.getMessage("hello.world", null, Locale.ENGLISH));
    log.info("hello pt: {}", messageSource.getMessage("hello.world", null, new Locale("pt")));
    log.info("validation: {}",
        validationMessageSource.getMessage("validation.success", null, Locale.ENGLISH));
  }
}
