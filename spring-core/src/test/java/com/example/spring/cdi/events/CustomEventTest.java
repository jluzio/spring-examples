package com.example.spring.cdi.events;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@SpringBootTest
@Slf4j
public class CustomEventTest {

  @Autowired
  ApplicationEventPublisher eventPublisher;

  @Test
  void test() {
    eventPublisher.publishEvent(new CustomEvent("-source-", "-data-"));
    eventPublisher.publishEvent(new CustomEvent("-source-", "foo"));

    eventPublisher.publishEvent(new StringEntityCreatedEvent("-source string-"));
    eventPublisher.publishEvent(new IntegerEntityCreatedEvent("-source integer-"));
  }

  @Configuration
  static class Config {

    @Component
    class EventListenerBean {

      @EventListener
        // defaults to @Order(0)
      void listenCustomEvent(CustomEvent event) {
        log.info("event: {}", event);
      }

      @Order(-1)
      @EventListener(condition = "#event.data == 'foo'")
      void listenCustomEventDataFoo(CustomEvent event) {
        log.info("event-data-foo: {}", event);
      }

      @Order(2)
      @EventListener(condition = "#root.event.data == 'foo'")
      void listenCustomEventRootDataFoo(CustomEvent someEvent) {
        log.info("event-root-data-foo: {}", someEvent);
      }

      @EventListener
      void listenEntityCreatedString(EntityCreatedEvent<String> event) {
        log.info("EntityCreatedEvent<String>: {}", event.getSource());
      }

      @EventListener
      void listenEntityCreatedInteger(EntityCreatedEvent<Integer> event) {
        log.info("EntityCreatedEvent<Integer>: {}", event.getSource());
      }
    }
  }

  class StringEntityCreatedEvent extends EntityCreatedEvent<String> {

    public StringEntityCreatedEvent(Object source) {
      super(source);
    }
  }


  class IntegerEntityCreatedEvent extends EntityCreatedEvent<Integer> {

    public IntegerEntityCreatedEvent(Object source) {
      super(source);
    }
  }
}
