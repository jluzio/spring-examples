package com.example.spring.core.events;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.context.ApplicationEvent;

@ToString
@Getter
@Setter
public class CustomEvent extends ApplicationEvent {

  private String data;

  public CustomEvent(Object source, String data) {
    super(source);
    this.data = data;
  }
}
