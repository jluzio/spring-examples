package com.example.spring.framework.events.concurrent;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

import java.time.Duration;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import reactor.core.publisher.Mono;

@SpringBootTest
@Slf4j
class AsyncEventTest {

  @Configuration
  @Import({EventListenerBean.class, ConcurrentEventRecorder.class})
  @EnableAsync
  static class Config {

  }

  @Autowired
  ApplicationEventPublisher eventPublisher;
  @Autowired
  ConcurrentEventRecorder eventRecorder;

  /**
   * From docs: By default, event listeners receive events synchronously.
   */
  @Test
  void basic_test() {
    eventPublisher.publishEvent(new ConcurrentEvent(this, "-data1-"));
    assertThat(eventRecorder.getEvents())
        .satisfies(it -> log.debug("events: {}", it))
        .isEmpty();

    eventPublisher.publishEvent(new ConcurrentEvent(this, "-data2-"));
    assertThat(eventRecorder.getEvents())
        .satisfies(it -> log.debug("events: {}", it))
        .isEmpty();

    await()
        .until(() -> eventRecorder.getEvents().size() == 2);
    log.debug("events: {}", eventRecorder.getEvents());
  }

  static class EventListenerBean extends AbstractConcurrentEventStoreEventListener {

    public EventListenerBean(ConcurrentEventRecorder eventRecorder) {
      super(
          eventRecorder,
          evt -> Mono.delay(Duration.ofMillis(100)).block()
      );
    }

    @EventListener
    @Async
    @Override
    void processEvent(ConcurrentEvent event) {
      super.processEvent(event);
    }
  }
}
