package com.example.spring.cdi;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Slf4j
public class LifecycleTest {
  @Autowired
  private LifecycleBean lifecycleBean;

  @Test
  public void test() {
    log.info("bean1: {}", lifecycleBean);
  }

}
