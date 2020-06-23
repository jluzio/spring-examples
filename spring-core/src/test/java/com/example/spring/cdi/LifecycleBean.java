package com.example.spring.cdi;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LifecycleBean {
  public LifecycleBean() {
    super();
    log("new");
  }

  public void init() {
    log("init");
  }

  public void destroy() {
    log("destroy");
  }

  protected void log(String context) {
    log.info("{}::{}", getClass().getSimpleName(), context);
  }
}
