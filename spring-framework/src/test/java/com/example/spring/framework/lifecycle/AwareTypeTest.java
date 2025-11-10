package com.example.spring.framework.lifecycle;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootTest
@Slf4j
class AwareTypeTest implements ApplicationContextAware {

  private ApplicationContext applicationContext;
  @Autowired
  private ApplicationContext autowiredApplicationContext;

  @Override
  public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
    this.applicationContext = applicationContext;
  }

  @Test
  void test() {
    log.info("context: {}", applicationContext);
    log.info("context: {}", autowiredApplicationContext);
    ConfigurableApplicationContext cfgContext = (ConfigurableApplicationContext) applicationContext;
    log.info("context: {}", cfgContext);

    var awares = List.of(
        "ApplicationContextAware", "ApplicationEventPublisherAware", "BeanClassLoaderAware",
        "BeanFactoryAware", "BeanNameAware", "BootstrapContextAware",
        "LoadTimeWeaverAware", "MessageSourceAware", "NotificationPublisherAware",
        "ResourceLoaderAware", "ServletConfigAware", "ServletContextAware");
    log.info("--- Available Awares---");
    awares.stream()
        .forEach(log::info);
  }

}
