package com.example.spring.core.environment.conditional;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

@Slf4j
class ConditionalOnMissingBeanTest {

  @Import({SomeServiceConditional1.class, SomeServiceConditional2.class, SomeServiceNotConditional.class})
  static class Scenario1Config1 {

  }

  @Test
  void test_scenario1() {
    appCtxRunner(Scenario1Config1.class).run(ctx -> {
      Map<String, SomeService> someServiceMap = ctx.getBeansOfType(SomeService.class);
      log.info(someServiceMap.toString());
      List<Class<?>> expected = List.of(SomeServiceConditional1.class, SomeServiceNotConditional.class);
      assertThat(someServiceMap.values())
          .hasSize(2)
          .map(Object::getClass)
          .isEqualTo(expected);
    });
  }


  @Import({SomeServiceConditional2.class, SomeServiceConditional1.class, SomeServiceNotConditional.class})
  static class Scenario2Config1 {

  }

  @Test
  void test_scenario2() {
    appCtxRunner(Scenario2Config1.class).run(ctx -> {
      Map<String, SomeService> someServiceMap = ctx.getBeansOfType(SomeService.class);
      log.info(someServiceMap.toString());
      List<Class<?>> expected = List.of(SomeServiceConditional2.class, SomeServiceNotConditional.class);
      assertThat(someServiceMap.values())
          .hasSize(2)
          .map(Object::getClass)
          .isEqualTo(expected);
    });
  }


  @Import({SomeServiceConditional1.class, SomeServiceConditional2.class})
  // LOWEST_PRECEDENCE, similar to AutoConfiguration, in which the @ConditionalOfMissingBean is recommended for
  // @Order(Ordered.LOWEST_PRECEDENCE)
  static class Scenario3Config1 {

  }

  @Order(Ordered.HIGHEST_PRECEDENCE)
  @Import({SomeServiceNotConditional.class})
  static class Scenario3Config2 {

  }

  @Test
  void test_scenario3() {
    appCtxRunner(Scenario3Config1.class, Scenario3Config2.class).run(ctx -> {
      Map<String, SomeService> someServiceMap = ctx.getBeansOfType(SomeService.class);
      log.info(someServiceMap.toString());
      List<Class<?>> expected = List.of(SomeServiceNotConditional.class);
      assertThat(someServiceMap.values())
          .hasSize(1)
          .map(Object::getClass)
          .isEqualTo(expected);
    });
  }


  @Import({SomeServiceConditional1.class, SomeServiceConditional2.class})
  static class Scenario4Config1 {

    @Bean
    @ConditionalOnMissingBean(SomeService.class)
    SomeServiceConditional1 someServiceConditional1() {
      return new SomeServiceConditional1();
    }

    @Bean
    @ConditionalOnMissingBean(SomeService.class)
    SomeServiceConditional2 someServiceConditional2() {
      return new SomeServiceConditional2();
    }
  }

  static class Scenario4Config2 {

    @Bean
    SomeServiceNotConditional someServiceNotConditional() {
      return new SomeServiceNotConditional();
    }
  }

  @Test
  void test_scenario4() {
    appCtxRunner(Scenario4Config1.class, Scenario4Config2.class).run(ctx -> {
      Map<String, SomeService> someServiceMap = ctx.getBeansOfType(SomeService.class);
      log.info(someServiceMap.toString());
      List<Class<?>> expected = List.of(SomeServiceConditional1.class, SomeServiceNotConditional.class);
      assertThat(someServiceMap.values())
          .hasSize(2)
          .map(Object::getClass)
          .isEqualTo(expected);
    });
  }

  interface SomeService {

  }

  @ConditionalOnMissingBean(SomeService.class)
  static class SomeServiceConditional1 implements SomeService {

  }

  @ConditionalOnMissingBean(SomeService.class)
  static class SomeServiceConditional2 implements SomeService {

  }

  static class SomeServiceNotConditional implements SomeService {

  }

  ApplicationContextRunner appCtxRunner(Class<?>... configurationClasses) {
    return new ApplicationContextRunner()
        .withUserConfiguration(configurationClasses);
  }

}
