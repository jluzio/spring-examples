package com.example.spring.messaging.kafka.course.basics.runner;

import com.example.spring.messaging.kafka.course.basics.config.AppConfig;
import jakarta.annotation.PostConstruct;
import java.util.function.Consumer;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.core.KafkaAdmin;

class BasicDemoRunner {

  public final org.apache.logging.log4j.Logger log =
      org.apache.logging.log4j.LogManager.getLogger(getClass());

  @Configuration
  @Import({AppConfig.class})
  static class BaseConfig {

  }

  @Autowired
  protected AppConfig config;


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
