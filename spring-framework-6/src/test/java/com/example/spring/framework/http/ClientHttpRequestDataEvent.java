package com.example.spring.framework.http;

import lombok.Getter;
import lombok.ToString;
import org.springframework.context.ApplicationEvent;

@ToString
@Getter
public class ClientHttpRequestDataEvent extends ApplicationEvent {

  private final ClientHttpRequestData requestData;

  public ClientHttpRequestDataEvent(Object source, ClientHttpRequestData requestData) {
    super(source);
    this.requestData = requestData;
  }
}
