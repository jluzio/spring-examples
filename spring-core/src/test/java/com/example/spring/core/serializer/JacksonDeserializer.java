package com.example.spring.core.serializer;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.InputStream;
import lombok.RequiredArgsConstructor;
import org.springframework.core.serializer.Deserializer;

@RequiredArgsConstructor
public class JacksonDeserializer implements Deserializer<Object> {

  private final ObjectMapper objectMapper;
  private final JavaType javaType;

  @Override
  public Object deserialize(InputStream inputStream) throws IOException {
    return objectMapper.readValue(inputStream, javaType);
  }
}
