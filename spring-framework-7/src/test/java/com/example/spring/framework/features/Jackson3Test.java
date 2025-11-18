package com.example.spring.framework.features;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.spring.framework.features.Jackson3Test.PostJacksonComponent;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jackson.JacksonComponent;
import org.springframework.boot.jackson.ObjectValueDeserializer;
import org.springframework.boot.jackson.ObjectValueSerializer;
import org.springframework.boot.jackson.autoconfigure.JacksonAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import tools.jackson.core.JsonGenerator;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.SerializationContext;

@SpringBootTest(classes = {JacksonAutoConfiguration.class, PostJacksonComponent.class})
class Jackson3Test {

  // @formatter:off
  record User(String id, String username, String email) {}
  record Post(String id, String text, String userId) {}
  record Foo(String id) {}

  @JacksonComponent
  static class PostJacksonComponent {
    static class Serializer extends ObjectValueSerializer<Post> {
      @Override
      protected void serializeObject(Post value, JsonGenerator jgen, SerializationContext context) {
        jgen
            .writeStringProperty("_id", value.id())
            .writeStringProperty("_text", value.text())
            .writeStringProperty("_userId", value.userId());
      }
    }

    static class Deserializer extends ObjectValueDeserializer<Post> {
      @Override
      protected Post deserializeObject(JsonParser jsonParser, DeserializationContext context, JsonNode tree) {
        return new Post(
            nullSafeValue(tree.get("_id"), String.class),
            nullSafeValue(tree.get("_text"), String.class),
            nullSafeValue(tree.get("_userId"), String.class));
      }
    }
  }
  // @formatter:on

  @Autowired
  ObjectMapper objectMapper;

  @Test
  void basics() {
    var pojo = new User("id", "user", "user@mail.org");

    var json = objectMapper.writeValueAsString(pojo);
    IO.println(json);

    var deserPojo = objectMapper.readValue(json, User.class);
    assertThat(deserPojo)
        .isEqualTo(pojo);
  }

  @Test
  void jacksonComponent() {
    var pojo = new Post("id", "some post", "userId1");

    var json = objectMapper.writeValueAsString(pojo);
    IO.println(json);
    assertThat(json).isEqualTo("""
        {"_id":"id","_text":"some post","_userId":"userId1"}""");

    var deserPojo = objectMapper.readValue(json, Post.class);
    assertThat(deserPojo)
        .isEqualTo(pojo);
  }
}
