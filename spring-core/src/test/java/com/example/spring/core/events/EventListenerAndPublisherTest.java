package com.example.spring.core.events;

import static org.assertj.core.api.Assertions.assertThat;

import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.event.EventListener;
import org.springframework.test.context.event.ApplicationEvents;
import org.springframework.test.context.event.RecordApplicationEvents;

@SpringBootTest
@RecordApplicationEvents
@Slf4j
class EventListenerAndPublisherTest {

  @Configuration
  @Import({EventListenerBean.class})
  static class Config {

  }

  @Autowired
  ApplicationEventPublisher eventPublisher;
  @Autowired
  ApplicationEvents applicationEvents;

  @Test
  void basic() {
    eventPublisher.publishEvent(new SourceEvent(this, "-data-"));

    assertThat(applicationEvents.stream(SourceEvent.class).count())
        .isEqualTo(1);
    assertThat(applicationEvents.stream(TargetEvent.class).count())
        .isEqualTo(1);
  }

  static class EventListenerBean {

    @EventListener
    TargetEvent listenSourceEventAndPublishTargetEvent(SourceEvent event) {
      log.info("event: {}", event);
      return new TargetEvent(this, "target :: %s".formatted(event.getData()));
    }

    @EventListener
    void listenTargetEvent(TargetEvent event) {
      log.info("event: {}", event);
    }
  }

  @ToString
  @Getter
  static class SourceEvent extends ApplicationEvent {

    private final String data;

    public SourceEvent(Object source, String data) {
      super(source);
      this.data = data;
    }
  }

  @ToString
  @Getter
  static class TargetEvent extends ApplicationEvent {

    private final String data;

    public TargetEvent(Object source, String data) {
      super(source);
      this.data = data;
    }
  }
}
