package com.example.spring.framework.events.concurrent;

import java.util.function.Consumer;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Getter
@Slf4j
abstract class AbstractConcurrentEventStoreEventListener {

  private final ConcurrentEventRecorder eventRecorder;
  private final Consumer<ConcurrentEvent> eventConsumer;

  void processEvent(ConcurrentEvent event) {
    log.info("Event (start): {}", event);
    eventConsumer.accept(event);
    eventRecorder.recordEvent(event);
    log.info("Event (end): {}", event);
  }
}
