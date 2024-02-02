package com.example.spring.core.events;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.event.EventListener;
import org.springframework.test.context.event.BeforeTestExecutionEvent;
import org.springframework.test.context.event.BeforeTestMethodEvent;

@SpringBootTest
@Slf4j
class ApplicationListenerTest {

  @Configuration
  @Import({AppEventListener.class, EventListenerBean.class})
  static class Config {

  }

  @Test
  void test() {
    log.info("Note: standard events");
    List.of("ContextRefreshedEvent", "ContextStartedEvent", "ContextStoppedEvent",
            "ContextClosedEvent", "RequestHandledEvent", "ServletRequestHandledEvent")
        .forEach(log::info);
  }

  static class AppEventListener implements ApplicationListener<ApplicationEvent> {

    @Override
    public void onApplicationEvent(ApplicationEvent event) {
      log.info("event: {}", event);
    }
  }

  static class EventListenerBean {

    @EventListener({BeforeTestMethodEvent.class, BeforeTestExecutionEvent.class})
    void listenSomeEvent(ApplicationEvent event) {
      log.info("some-event: {}", event);
    }
  }
}
