package com.example.spring.core.events.concurrent;

import java.util.EventObject;
import lombok.Getter;
import lombok.ToString;
import org.springframework.context.ApplicationEvent;

@ToString
@Getter
class ConcurrentEvent extends ApplicationEvent {

  private final String data;

  public ConcurrentEvent(Object source, String data) {
    super(source);
    this.data = data;
  }
}
