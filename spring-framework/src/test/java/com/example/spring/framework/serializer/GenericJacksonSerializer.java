package com.example.spring.framework.serializer;

import java.io.IOException;
import java.io.OutputStream;
import lombok.RequiredArgsConstructor;
import org.springframework.core.serializer.Serializer;
import tools.jackson.databind.ObjectMapper;

@RequiredArgsConstructor
public class GenericJacksonSerializer implements Serializer<Object> {

  private final ObjectMapper objectMapper;

  @Override
  public void serialize(Object object, OutputStream outputStream) throws IOException {
    var className = object == null ? null : object.getClass().getName();
    var serializationObject = new GenericSerializationObject(className, object);
    objectMapper.writeValue(outputStream, serializationObject);
  }
}
