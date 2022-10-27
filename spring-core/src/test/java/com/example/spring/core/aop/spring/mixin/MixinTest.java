package com.example.spring.core.aop.spring.mixin;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.DeclareMixin;
import org.aspectj.lang.annotation.DeclareParents;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.aop.AopAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Component;

@SpringBootTest(properties = {
    "aop.mixin.type=DeclareParents",
//    "aop.mixin.type=DeclareMixin",
})
@Slf4j
class MixinTest {

  @Configuration
  @Import(AopAutoConfiguration.class)
  static class Config {

    @Bean
    ClassicPerformer classicActor() {
      return new ClassicActor();
    }

    @Component
    @Aspect
    @ConditionalOnProperty(value = "aop.mixin.type", havingValue = "DeclareParents")
    public static class DeclareParentsAspect {

      @DeclareParents(value = "com.example.spring.core.aop.spring.mixin.ClassicPerformer+", defaultImpl = ImprovActor.class)
      public static ImprovisationPerformer improvisationPerformer;
    }

    @Component
    @Aspect
    @ConditionalOnProperty(value = "aop.mixin.type", havingValue = "DeclareMixin")
    public static class DeclareMixinAspect {

      @DeclareMixin("com.example.spring.core.aop.spring.mixin.ClassicPerformer+")
      public static ImprovisationPerformer createImprovisationPerformer() {
        return new ImprovActor();
      }
    }
  }

  @Autowired
  ClassicPerformer actor;

  @Test
  void test() throws Throwable {
    log.info("actor: {}", actor);
    log.info("actor: classic={}", actor instanceof ClassicPerformer);
    log.info("actor: improvisation={}", actor instanceof ImprovisationPerformer);

    ClassicPerformer classicPerformer = new ClassicActor();
    log.info("classicPerformer: {}", classicPerformer);
    log.info("classicPerformer: classic={}", classicPerformer instanceof ClassicPerformer);
    log.info("classicPerformer: improvisation={}",
        classicPerformer instanceof ImprovisationPerformer);

    log.info("done!");
  }

}
