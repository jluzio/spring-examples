package com.example.spring.framework.serializer;

import java.io.IOException;
import java.io.InputStream;
import lombok.RequiredArgsConstructor;
import org.springframework.core.serializer.Deserializer;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.ObjectMapper;

@RequiredArgsConstructor
public class JacksonDeserializer implements Deserializer<Object> {

  private final ObjectMapper objectMapper;
  private final JavaType javaType;

  @Override
  public Object deserialize(InputStream inputStream) throws IOException {
    return objectMapper.readValue(inputStream, javaType);
  }
}
