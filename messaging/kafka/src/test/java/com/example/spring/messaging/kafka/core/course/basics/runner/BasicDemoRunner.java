package com.example.spring.messaging.kafka.core.course.basics.runner;

import com.example.spring.messaging.kafka.core.course.basics.config.AppConfig;
import jakarta.annotation.PostConstruct;
import java.util.function.Consumer;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.core.KafkaAdmin;

class BasicDemoRunner {

  @Import({AppConfig.class})
  static class BaseConfig {

  }

  final Logger log = LogManager.getLogger(getClass());
  @Autowired
  AppConfig config;


  public static <T> void runApp(Class<T> clazz, Consumer<T> runner) {
    try (var app = new SpringApplicationBuilder(clazz, BaseConfig.class)
        .web(WebApplicationType.NONE)
        .run()
    ) {
      T instance = app.getBean(clazz);
      runner.accept(instance);
    }
  }

  @PostConstruct
  public void setup() {
    setupTopics();
  }

  public void setupTopics() {
    var admin = new KafkaAdmin(config.basicKafkaConfig());
    admin.createOrModifyTopics(new NewTopic(config.defaultTopic(), 3, (short) 1));
  }

}
