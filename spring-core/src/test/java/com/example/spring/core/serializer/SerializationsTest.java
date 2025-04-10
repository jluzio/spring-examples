package com.example.spring.core.serializer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = {Serializations.class, JacksonAutoConfiguration.class})
class SerializationsTest {

  @Autowired
  Serializations serializations;
  @Autowired
  ObjectMapper objectMapper;

  @Test
  void java_serializable() throws IOException {
    var serialization = serializations.java();

    var value = serializableValue();

    var serializedValue = serialization.serializeToByteArray(value);
    assertThat(serializedValue)
        .isNotNull();

    var deserializedValue = serialization.deserializeFromByteArray(serializedValue);

    assertThat(deserializedValue)
        .isNotNull()
        .isEqualTo(value);
  }

  @Test
  void java_non_serializable() {
    var serialization = serializations.java();

    var value = nonSerializableValue();

    assertThatThrownBy(() -> serialization.serializeToByteArray(value))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Serializable");
  }

  @Test
  void json() throws IOException {
    var javaType = TypeFactory.defaultInstance().constructType(NonSerializableBean.class);
    var serialization = serializations.json(objectMapper, javaType);

    var value = nonSerializableValue();

    var serializedValue = serialization.serializeToByteArray(value);
    assertThat(serializedValue)
        .isNotNull();

    var deserializedValue = serialization.deserializeFromByteArray(serializedValue);

    assertThat(deserializedValue)
        .isNotNull()
        .isEqualTo(value);
  }

  @Test
  void json_generics() throws IOException {
    var typeReference = new TypeReference<ArrayList<NonSerializableBean>>() {
    };
    var javaType = TypeFactory.defaultInstance().constructType(typeReference);
    var serialization = serializations.json(objectMapper, javaType);

    var value = new ArrayList<>(List.of(nonSerializableValue()));

    var serializedValue = serialization.serializeToByteArray(value);
    assertThat(serializedValue)
        .isNotNull();

    var deserializedValue = serialization.deserializeFromByteArray(serializedValue);

    assertThat(deserializedValue)
        .isNotNull()
        .isEqualTo(value);
  }

  @Test
  void json_null() throws IOException {
    var javaType = TypeFactory.unknownType();
    var serialization = serializations.json(objectMapper, javaType);

    ObjectMapper value = null;

    var serializedValue = serialization.serializeToByteArray(value);
    assertThat(serializedValue)
        .isNotNull();

    var deserializedValue = serialization.deserializeFromByteArray(serializedValue);

    assertThat(deserializedValue)
        .isEqualTo(value);
  }

  @Test
  void genericJson() throws IOException {
    var serialization = serializations.genericJson(objectMapper);

    var value = nonSerializableValue();

    var serializedValue = serialization.serializeToByteArray(value);
    assertThat(serializedValue)
        .isNotNull();

    var deserializedValue = serialization.deserializeFromByteArray(serializedValue);

    assertThat(deserializedValue)
        .isNotNull()
        .isEqualTo(value);
  }

  @Test
  void genericJson_null() throws IOException {
    var serialization = serializations.genericJson(objectMapper);

    ObjectMapper value = null;

    var serializedValue = serialization.serializeToByteArray(value);
    assertThat(serializedValue)
        .isNotNull();

    var deserializedValue = serialization.deserializeFromByteArray(serializedValue);

    assertThat(deserializedValue)
        .isEqualTo(value);
  }

  private SerializableBean serializableValue() {
    return new SerializableBean("foo", "bar");
  }

  private NonSerializableBean nonSerializableValue() {
    return new NonSerializableBean("foo", "bar");
  }

  record SerializableBean(String value1, String value2) implements Serializable {

  }

  record NonSerializableBean(String value1, String value2) {

  }

}