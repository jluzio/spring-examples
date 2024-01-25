package com.example.spring.boot.playground.repository;

import java.lang.reflect.Parameter;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
class ParametersCompilerTest {

  interface Sample {

    void someMethod(String name, String email);
  }

  @Test
  void test() throws Exception {
    Stream.of(Sample.class.getMethods())
        .filter(m -> m.getName().equals("someMethod"))
        .flatMap(m -> Stream.of(m.getParameters()))
        .map(Parameter::getName)
        .forEach(v -> log.info("param: {}", v));
  }

}
