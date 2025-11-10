package com.example.spring.framework.serializer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.InputStream;
import lombok.RequiredArgsConstructor;
import org.springframework.core.serializer.Deserializer;

@RequiredArgsConstructor
public class GenericJacksonDeserializer implements Deserializer<Object> {

  private final ObjectMapper objectMapper;

  @Override
  public Object deserialize(InputStream inputStream) throws IOException {
    JsonNode rootNode = objectMapper.readTree(inputStream);
    JsonNode classNameNode = rootNode.path(GenericSerializationObject.JSON_PROPERTY_CLASS_NAME);
    JsonNode valueNode = rootNode.path(GenericSerializationObject.JSON_PROPERTY_VALUE);

    String className = classNameNode.textValue();
    Class<?> clazz = resolveClass(className);

    return objectMapper.convertValue(valueNode, clazz);
  }

  private Class<?> resolveClass(String className) throws IOException {
    try {
      return className != null ? Class.forName(className) : Object.class;
    } catch (ClassNotFoundException e) {
      throw new IOException(e);
    }
  }
}
