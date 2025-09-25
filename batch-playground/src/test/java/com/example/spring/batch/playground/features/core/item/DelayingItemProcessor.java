package com.example.spring.batch.playground.features.core.item;

import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.ItemProcessor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class DelayingItemProcessor<I, O> implements ItemProcessor<I, O> {

  private final Duration delayDuration;
  private final ItemProcessor<I, O> delegateItemProcessor;

  @Override
  public O process(I item) throws Exception {
    Mono.delay(delayDuration).block();
    return delegateItemProcessor.process(item);
  }
}
