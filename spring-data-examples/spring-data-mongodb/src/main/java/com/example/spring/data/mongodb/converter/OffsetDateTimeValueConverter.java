package com.example.spring.data.mongodb.converter;

import java.time.OffsetDateTime;
import org.springframework.data.convert.PropertyValueConverter;
import org.springframework.data.convert.ValueConversionContext;

public class OffsetDateTimeValueConverter implements
    PropertyValueConverter<OffsetDateTime, String, ValueConversionContext<?>> {

  @Override
  public OffsetDateTime read(String value, ValueConversionContext<?> context) {
    return OffsetDateTime.parse(value);
  }

  @Override
  public String write(OffsetDateTime value, ValueConversionContext<?> context) {
    return value.toString();
  }
}