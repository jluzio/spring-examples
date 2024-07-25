package com.example.spring.messaging.kafka.course.basics.test;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit.jupiter.EnabledIf;

@SpringBootTest
@EnabledIf("#{systemEnvironment['COURSE_TEST'] == 'true'}")
class BaseTest {

  @Configuration
  static class Config {

  }

  @Value("${spring.cloud.stream.kafka.binder.brokers}")
  protected String bootstrapServers;


}
