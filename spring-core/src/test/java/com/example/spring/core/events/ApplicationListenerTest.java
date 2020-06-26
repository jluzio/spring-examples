package com.example.spring.core.events;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.test.context.event.BeforeTestExecutionEvent;
import org.springframework.test.context.event.BeforeTestMethodEvent;

@SpringBootTest
@Slf4j
public class ApplicationListenerTest {

  @Test
  void test() {
    log.info("Note: standard events");
    List.of("ContextRefreshedEvent", "ContextStartedEvent", "ContextStoppedEvent",
        "ContextClosedEvent", "RequestHandledEvent", "ServletRequestHandledEvent")
        .forEach(log::info);
  }

  @Configuration
  static class Config {

    @Component
    class AppEventListener implements ApplicationListener {

      @Override
      public void onApplicationEvent(ApplicationEvent event) {
        log.info("event: {}", event);
      }
    }

    @Component
    class EventListenerBean {

      @EventListener({BeforeTestMethodEvent.class, BeforeTestExecutionEvent.class})
      void listenSomeEvent(ApplicationEvent event) {
        log.info("some-event: {}", event);
      }

    }
  }
}
