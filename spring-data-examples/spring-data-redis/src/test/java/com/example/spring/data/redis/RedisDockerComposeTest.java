package com.example.spring.data.redis;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.spring.data.redis.model.SampleDataPojo;
import com.example.spring.data.redis.model.SampleDataRecord;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Duration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.data.redis.RedisConnectionDetails;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisClientConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@SpringBootTest(
    properties = {
        "spring.docker.compose.skip.in-tests=false",
        "spring.docker.compose.enabled=true",
        "spring.docker.compose.file=docker/redis.docker-compose.yml"
    }
)
class RedisDockerComposeTest {

  @TestConfiguration
//  @Import({JacksonAutoConfiguration.class})
  static class Config {

//    @Bean
    JedisConnectionFactory redisConnectionFactory(RedisConnectionDetails redisConnectionDetails) {
      // defaults to localhost:6379
      var redisConfig = redisConnectionDetails.getStandalone();
      RedisStandaloneConfiguration config = new RedisStandaloneConfiguration(
          redisConfig.getHost(),
          redisConfig.getPort());

      var defaultTimeout = Duration.ofSeconds(5);
      JedisClientConfiguration clientConfig = JedisClientConfiguration.builder()
          .readTimeout(defaultTimeout)
          .connectTimeout(defaultTimeout)
          .build();
      return new JedisConnectionFactory(config, clientConfig);
    }

    @Bean
    StringRedisTemplate stringRedisTemplate(RedisConnectionFactory redisConnectionFactory) {
      return new StringRedisTemplate(redisConnectionFactory);
    }

    @Bean
    RedisTemplate<String, SampleDataRecord> sampleDataRecordRedisTemplate(
        RedisConnectionFactory redisConnectionFactory) {

      Jackson2JsonRedisSerializer<SampleDataRecord> jackson2JsonRedisSerializer =
          new Jackson2JsonRedisSerializer<>(SampleDataRecord.class);

      StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();

      RedisTemplate<String, SampleDataRecord> redisTemplate = new RedisTemplate<>();
      redisTemplate.setConnectionFactory(redisConnectionFactory);
      redisTemplate.setKeySerializer(stringRedisSerializer);
      redisTemplate.setHashKeySerializer(stringRedisSerializer);
      redisTemplate.setValueSerializer(jackson2JsonRedisSerializer);
      redisTemplate.setHashValueSerializer(jackson2JsonRedisSerializer);
      redisTemplate.afterPropertiesSet();
      return redisTemplate;
    }

    @Bean
    RedisTemplate<String, Object> genericRedisTemplate(
        RedisConnectionFactory redisConnectionFactory) {

      // Does not work with records
      ObjectMapper mapper = new ObjectMapper();
      mapper.setVisibility(
          PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
      mapper.activateDefaultTyping(
          mapper.getPolymorphicTypeValidator(), ObjectMapper.DefaultTyping.NON_FINAL);
      Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer =
          new Jackson2JsonRedisSerializer<>(mapper, Object.class);

      StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();

      RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
      redisTemplate.setConnectionFactory(redisConnectionFactory);
      redisTemplate.setKeySerializer(stringRedisSerializer);
      redisTemplate.setHashKeySerializer(stringRedisSerializer);
      redisTemplate.setValueSerializer(jackson2JsonRedisSerializer);
      redisTemplate.setHashValueSerializer(jackson2JsonRedisSerializer);
      redisTemplate.afterPropertiesSet();
      return redisTemplate;
    }
  }

  @Autowired
  private ObjectMapper objectMapper;
  @Autowired
  private RedisTemplate<String, String> stringRedisTemplate;
  @Autowired
  private RedisTemplate<String, SampleDataRecord> sampleDataRecordRedisTemplate;
  @Autowired
  private RedisTemplate<String, Object> genericRedisTemplate;

  @Test
  void string_value() {
    String key = "test";
    var ops = stringRedisTemplate.opsForHash();

    ops.put(key, "foo", "FOO");
    assertThat(ops.get(key, "foo"))
        .isEqualTo("FOO");
  }

  @Test
  void sample_data_value() {
    String key = "test";
    var ops = sampleDataRecordRedisTemplate.opsForHash();

    SampleDataRecord sampleDataRecord = new SampleDataRecord("1", "data1");
    ops.put(key, "sampleData", sampleDataRecord);
    assertThat(ops.get(key, "sampleData"))
        .isEqualTo(sampleDataRecord);
  }

  @Test
  void generic_value() {
    String key = "test";
    var genericOps = genericRedisTemplate.opsForHash();
    var sampleDataRecordOps = sampleDataRecordRedisTemplate.opsForHash();

    SampleDataPojo sampleDataPojo = new SampleDataPojo("1", "data1");
    genericOps.put(key, "sampleDataPojo", sampleDataPojo);
    assertThat(genericOps.get(key, "sampleDataPojo"))
        .isEqualTo(sampleDataPojo);

    SampleDataRecord sampleDataRecord = new SampleDataRecord("1", "data1");
    genericOps.put(key, "sampleDataRecord", sampleDataRecord);
    assertThatThrownBy(() -> genericOps.get(key, "sampleDataRecord"))
        .isInstanceOf(SerializationException.class);

    sampleDataRecordOps.put(key, "sampleDataRecord2", sampleDataRecord);
    assertThat(sampleDataRecordOps.get(key, "sampleDataRecord2"))
        .isEqualTo(sampleDataRecord);
  }
}
