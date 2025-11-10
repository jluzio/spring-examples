package com.example.spring.framework.config.dependency;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.spring.framework.config.dependency.BeanMethodConfigBasicsTest.Config1;
import com.example.spring.framework.config.dependency.BeanMethodConfigBasicsTest.Config2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@SpringBootTest(classes = {Config1.class, Config2.class}, properties = "envVar: 42")
class BeanMethodConfigBasicsTest {

  // @formatter:off
  record Foo(String id, Bar bar, Baz baz) {}
  record Bar(String id) {}
  record Baz(String id) {}
  // @formatter:on

  @Configuration
  static class Config1 {

    @Bean
    Foo foo(Config2 config2) {
      for (int i = 0; i < 100; i++) {
        // gets the instance already in Spring context (due to proxy on the configuration)
        this.bar1(null);
        config2.baz1(null);
      }

      // gets the instance already in Spring context (due to proxy on the configuration)
      var bar = bar1(null);
      var baz = config2.baz1(null);

      return new Foo("foo1", bar, baz);
    }

    @Bean
    Bar bar1(Environment env) {
      IO.println("creating bar1");
      return new Bar("bar1 - %s".formatted(env.getProperty("envVar")));
    }

    @Bean
    Bar bar2(Environment env) {
      IO.println("creating bar2");
      return new Bar("bar2 - %s".formatted(env.getProperty("envVar")));
    }
  }

  @Configuration
  static class Config2 {

    @Bean
    Baz baz1(Environment env) {
      IO.println("creating baz1");
      return new Baz("baz1 - %s".formatted(env.getProperty("envVar")));
    }
  }

  @Autowired
  Foo foo;

  @Test
  void test() {
    assertThat(foo)
        .isEqualTo(new Foo("foo1", new Bar("bar1 - 42"), new Baz("baz1 - 42")));
  }

}
