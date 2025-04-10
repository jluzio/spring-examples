package com.example.spring.core.serializer;

import com.fasterxml.jackson.annotation.JsonProperty;

public record GenericSerializationObject(
    @JsonProperty(JSON_PROPERTY_CLASS_NAME) String className,
    @JsonProperty(JSON_PROPERTY_VALUE) Object value
) {

  public static final String JSON_PROPERTY_CLASS_NAME = "className";
  public static final String JSON_PROPERTY_VALUE = "value";

}
