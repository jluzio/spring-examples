package com.example.spring.core.serializer;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.serializer.support.SerializationDelegate;
import org.springframework.stereotype.Component;

@Component
public class Serializations {

  public SerializationDelegate java() {
    return new SerializationDelegate(Thread.currentThread().getContextClassLoader());
  }

  public SerializationDelegate json(ObjectMapper objectMapper, JavaType javaType) {
    return new SerializationDelegate(
        new JacksonSerializer(objectMapper),
        new JacksonDeserializer(objectMapper, javaType)
    );
  }

  public SerializationDelegate genericJson(ObjectMapper objectMapper) {
    return new SerializationDelegate(
        new GenericJacksonSerializer(objectMapper),
        new GenericJacksonDeserializer(objectMapper)
    );
  }

}
