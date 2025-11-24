package com.example.spring.framework.serializer;

import org.springframework.core.serializer.support.SerializationDelegate;
import org.springframework.stereotype.Component;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.ObjectMapper;

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
