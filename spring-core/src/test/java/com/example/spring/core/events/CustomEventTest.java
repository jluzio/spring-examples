package com.example.spring.core.events;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.clearInvocations;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@SpringBootTest
@Slf4j
class CustomEventTest {

  @Configuration
  @Import(EventListenerBean.class)
  static class Config {

  }

  @Autowired
  ApplicationEventPublisher eventPublisher;
  @SpyBean
  EventListenerBean eventListener;

  @Test
  void basic() {
    eventPublisher.publishEvent(new CustomEvent(this, "-data-"));
    verify(eventListener).listenCustomEvent(any());
    verify(eventListener, never()).listenCustomEventDataFoo(any());
    verify(eventListener, never()).listenCustomEventRootDataFoo(any());
    clearInvocations(eventListener);

    eventPublisher.publishEvent(new CustomEvent(this, "foo"));
    verify(eventListener).listenCustomEvent(any());
    verify(eventListener).listenCustomEventDataFoo(any());
    verify(eventListener).listenCustomEventRootDataFoo(any());
    clearInvocations(eventListener);

    eventPublisher.publishEvent(new StringEntityCreatedEvent(this));
    verify(eventListener).listenEntityCreatedString(any());
    verify(eventListener, never()).listenEntityCreatedInteger(any());
    clearInvocations(eventListener);

    eventPublisher.publishEvent(new IntegerEntityCreatedEvent(this));
    verify(eventListener, never()).listenEntityCreatedString(any());
    verify(eventListener).listenEntityCreatedInteger(any());
    clearInvocations(eventListener);

    eventPublisher.publishEvent(new NonApplicationEvent("-data-"));
    verify(eventListener).listenNonApplicationEvent(any());
    clearInvocations(eventListener);
  }

  @Component
  static class EventListenerBean {

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
      log.info("EntityCreatedEvent<String>: {}", event);
    }

    @EventListener
    void listenEntityCreatedInteger(EntityCreatedEvent<Integer> event) {
      log.info("EntityCreatedEvent<Integer>: {}", event);
    }

    @EventListener
    void listenNonApplicationEvent(NonApplicationEvent event) {
      log.info("NonApplicationEvent: {}", event);
    }
  }

  @ToString
  @Getter
  static class CustomEvent extends ApplicationEvent {

    private final String data;

    public CustomEvent(Object source, String data) {
      super(source);
      this.data = data;
    }
  }

  static class StringEntityCreatedEvent extends EntityCreatedEvent<String> {

    public StringEntityCreatedEvent(Object source) {
      super(source);
    }
  }

  static class IntegerEntityCreatedEvent extends EntityCreatedEvent<Integer> {

    public IntegerEntityCreatedEvent(Object source) {
      super(source);
    }
  }

  @ToString
  @Getter
  static class EntityCreatedEvent<T> extends ApplicationEvent {

    public EntityCreatedEvent(Object source) {
      super(source);
    }
  }

  record NonApplicationEvent(String data) {

  }
}
