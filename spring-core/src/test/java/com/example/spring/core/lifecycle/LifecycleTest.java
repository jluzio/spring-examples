package com.example.spring.core.lifecycle;

import com.example.spring.core.lifecycle.LifecycleTest.Config.LifecycleBean;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.Lifecycle;
import org.springframework.stereotype.Component;

@SpringBootTest
@Slf4j
class LifecycleTest {

  @Autowired
  private LifecycleBean lifecycleBean;

  @TestConfiguration
  public static class Config {

    @Component
    @Slf4j
    public static class LifecycleBean implements InitializingBean, DisposableBean, Lifecycle {

      private boolean running = false;

      public LifecycleBean() {
        super();
        log("new");
      }

      @PostConstruct
      public void annotationInit() {
        log("annotationInit");
      }

      @PreDestroy
      public void annotationDestroy() {
        log("annotationDestroy");
      }

      @Override
      public void afterPropertiesSet() throws Exception {
        log("afterPropertiesSet");
      }

      @Override
      public void destroy() throws Exception {
        log("destroy");
      }

      @Override
      public void start() {
        log("start");
        running = true;
      }

      @Override
      public void stop() {
        log("stop");
        running = false;
      }

      @Override
      public boolean isRunning() {
        log("start");
        return running;
      }

      protected void log(String context) {
        log.info("{}::{}", getClass().getSimpleName(), context);
      }
    }

  }

  @Test
  public void test() {
    log.info("bean1: {}", lifecycleBean);
  }

}
