package com.example.spring.framework.events.concurrent;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Synchronized;

public class ConcurrentEventRecorder {

  /**
   * Consider using a synchronized collection
   *
   * @see java.util.Collections#synchronizedList(List)
   */
  @Getter(onMethod_ = {@Synchronized})
  private final List<Object> events = new ArrayList<>();

  @Synchronized
  public void recordEvent(Object event) {
    events.add(event);
  }

}
