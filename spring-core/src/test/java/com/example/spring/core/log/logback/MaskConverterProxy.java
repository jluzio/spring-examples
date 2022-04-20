package com.example.spring.core.log.logback;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.pattern.CompositeConverter;
import lombok.Getter;
import lombok.Setter;

public class MaskConverterProxy extends CompositeConverter<ILoggingEvent> {

  @Getter
  @Setter
  private static MaskConverter converter;

  @Override
  protected String transform(ILoggingEvent event, String in) {
    return converter == null
        ? in
        : converter.transform(event, in);
  }
}
