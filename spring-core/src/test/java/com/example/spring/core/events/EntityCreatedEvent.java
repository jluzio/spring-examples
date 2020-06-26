package com.example.spring.core.events;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.context.ApplicationEvent;

@ToString
@Getter
@Setter
public class EntityCreatedEvent<T> extends ApplicationEvent {

  public EntityCreatedEvent(Object source) {
    super(source);
  }
}
