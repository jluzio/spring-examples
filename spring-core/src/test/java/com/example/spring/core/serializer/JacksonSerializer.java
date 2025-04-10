package com.example.spring.core.serializer;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.OutputStream;
import lombok.RequiredArgsConstructor;
import org.springframework.core.serializer.Serializer;

@RequiredArgsConstructor
public class JacksonSerializer implements Serializer<Object> {

  private final ObjectMapper objectMapper;

  @Override
  public void serialize(Object object, OutputStream outputStream) throws IOException {
    objectMapper.writeValue(outputStream, object);
  }
}
