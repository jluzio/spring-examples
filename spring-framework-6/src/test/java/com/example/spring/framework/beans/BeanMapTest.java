package com.example.spring.framework.beans;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;
import java.util.Map.Entry;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@SpringBootTest
@Slf4j
class BeanMapTest {

  @Configuration
  static class Config {

    @Bean("processor1")
    Processor processor1() {
      return new ProcessorBean("id1");
    }

    @Bean("processor2")
    Processor processor2() {
      return new ProcessorBean("id2");
    }

    @Bean("processor3")
    Processor processor3() {
      return new ProcessorBean("id3");
    }

  }

  @Autowired
  Map<String, Processor> processorMap;

  @Test
  void test() {
    log.debug("{}", processorMap);
    assertThat(processorMap)
        .containsKeys("processor1", "processor2", "processor3");
    assertThat(processorMap)
        .extractingFromEntries(Entry::getValue)
        .extracting("id")
        .contains("id1", "id2", "id3");
  }

  interface Processor {

    void process();
  }

  record ProcessorBean(String id) implements Processor {

    @Override
    public void process() {
      log.debug("{}::process", id);
    }
  }
}
