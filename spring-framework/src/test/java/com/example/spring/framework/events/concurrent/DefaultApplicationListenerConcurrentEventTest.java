package com.example.spring.framework.events.concurrent;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableAsync;
import reactor.core.publisher.Mono;

@SpringBootTest
@Slf4j
class DefaultApplicationListenerConcurrentEventTest {

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
   * From docs: <code>supportsAsyncExecution=true</code> supports async depending on the multicaster configuration, which by default in sync.
   * @see ApplicationListener#supportsAsyncExecution()
   */
  @Test
  void basic_test() {
    eventPublisher.publishEvent(new ConcurrentEvent(this, "-data1-"));
    assertThat(eventRecorder.getEvents())
        .satisfies(it -> log.debug("events: {}", it))
        .hasSize(1);

    eventPublisher.publishEvent(new ConcurrentEvent(this, "-data2-"));
    assertThat(eventRecorder.getEvents())
        .satisfies(it -> log.debug("events: {}", it))
        .hasSize(2);
  }

  static class EventListenerBean extends AbstractConcurrentEventStoreEventListener
      implements ApplicationListener<ConcurrentEvent> {

    public EventListenerBean(ConcurrentEventRecorder eventRecorder) {
      super(
          eventRecorder,
          evt -> Mono.delay(Duration.ofMillis(100)).block()
      );
    }

    @Override
    public void onApplicationEvent(ConcurrentEvent event) {
      processEvent(event);
    }
  }
}
