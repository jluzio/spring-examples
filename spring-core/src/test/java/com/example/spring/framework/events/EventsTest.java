package com.example.spring.framework.events;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.stream.Stream;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.test.context.event.ApplicationEvents;
import org.springframework.test.context.event.RecordApplicationEvents;

@SpringBootTest
@RecordApplicationEvents
@Slf4j
class EventsTest {

  @Configuration
  static class Config {

    @EventListener({CustomEvent.class, CustomNonApplicationEvent.class})
    void listen(Object event) {
      log.info("event: {}", event);
    }
  }

  @Getter
  static class CustomEvent extends ApplicationEvent {

    private final String data;

    public CustomEvent(Object source, String data) {
      super(source);
      this.data = data;
    }
  }

  record CustomNonApplicationEvent(String data) {

  }

  @Autowired
  ApplicationEventPublisher eventPublisher;
  @Autowired
  ApplicationEvents applicationEvents;

  @Test
  void test() {
    eventPublisher.publishEvent(new CustomEvent(this, "some-data"));
    assertThat(applicationEvents.stream().count())
        .isGreaterThan(1);
    assertThat(applicationEvents.stream(CustomEvent.class).count())
        .isEqualTo(1);
    Stream<CustomEvent> filteredEvents = applicationEvents.stream(CustomEvent.class)
        .filter(e -> Objects.equals(e.data, "some-data"));
    assertThat(filteredEvents.count())
        .isEqualTo(1);

    eventPublisher.publishEvent(new CustomNonApplicationEvent("some-data"));
    assertThat(applicationEvents.stream(CustomNonApplicationEvent.class).count())
        .isEqualTo(1);
  }

}
