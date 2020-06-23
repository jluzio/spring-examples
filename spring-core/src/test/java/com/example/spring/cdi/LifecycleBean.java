package com.example.spring.cdi;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.Lifecycle;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class LifecycleBean implements InitializingBean, DisposableBean, Lifecycle {

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
