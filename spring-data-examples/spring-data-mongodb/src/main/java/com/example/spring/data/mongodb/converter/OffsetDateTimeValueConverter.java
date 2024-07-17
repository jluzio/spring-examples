package com.example.spring.data.mongodb.converter;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import org.bson.BsonDateTime;
import org.springframework.data.convert.PropertyValueConverter;
import org.springframework.data.convert.ValueConversionContext;

public class OffsetDateTimeValueConverter implements
    PropertyValueConverter<OffsetDateTime, BsonDateTime, ValueConversionContext<?>> {

  @Override
  public OffsetDateTime read(BsonDateTime value, ValueConversionContext<?> context) {
    return Instant.ofEpochMilli(value.getValue()).atOffset(ZoneOffset.UTC);
  }

  @Override
  public BsonDateTime write(OffsetDateTime value, ValueConversionContext<?> context) {
    return new BsonDateTime(value.toInstant().toEpochMilli());
  }
}