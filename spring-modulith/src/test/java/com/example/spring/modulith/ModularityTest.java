package com.example.spring.modulith;

import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;
import org.springframework.modulith.docs.Documenter;

class ModularityTest {

  @Test
  void verifyModularity( ) {
    var modules = ApplicationModules.of(ModulithApplication.class);

    modules.verify();

    IO.println(modules);

    new Documenter(modules)
        .writeDocumentation();
  }

}
