package com.example.spring.core.environment.conditional;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Profiles;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.util.MultiValueMap;

@SpringBootTest
@Slf4j
@ActiveProfiles("test")
public class ConditionalTest {

  @Autowired(required = false)
  List<String> beans;

  @Test
  void test() {
    log.info("beans: {}", beans);
  }

  @Configuration
  @Profile("prd")
  @Conditional({Condition1.class, Condition2.class})
  static class Config {

    @Bean
    String bean1() {
      return "bean1";
    }
  }

  // copy of Profile condition
  static class Condition1 implements Condition {

    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
      // Read the @Profile annotation attributes
      MultiValueMap<String, Object> attrs =
          metadata.getAllAnnotationAttributes(Profile.class.getName());
      if (attrs != null) {
        for (Object value : attrs.get("value")) {
          if (context.getEnvironment().acceptsProfiles(Profiles.of((String[]) value))) {
            return true;
          }
        }
        return false;
      }
      return true;
    }
  }

  static class Condition2 implements Condition {

    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
      return true;
    }
  }

}
