package com.example.spring.modulith;

import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;

class ModularityTest {

  @Test
  void verifyModularity( ) {
    ApplicationModules.of(ModulithApplication.class)
        .verify();
  }

}
