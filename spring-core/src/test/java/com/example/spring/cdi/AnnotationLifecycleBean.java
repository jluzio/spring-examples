package com.example.spring.cdi;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import org.springframework.stereotype.Component;

@Component
public class AnnotationLifecycleBean extends LifecycleBean {
  @Override
  @PostConstruct
  public void init() {
    super.init();
  }

  @Override
  @PreDestroy
  public void destroy() {
    super.destroy();
  }
}
