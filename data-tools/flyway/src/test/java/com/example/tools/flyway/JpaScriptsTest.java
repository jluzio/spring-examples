package com.example.tools.flyway;


import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.core.io.Resource;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest(properties = "spring.flyway.enabled: false")
@Slf4j
@ActiveProfiles({"h2-mem", "jpa-gen-scripts"})
class JpaScriptsTest {

  @Value("file:${spring.jpa.properties.jakarta.persistence.schema-generation.scripts.create-target}")
  Resource createTarget;
  @Value("file:${spring.jpa.properties.jakarta.persistence.schema-generation.scripts.drop-target}")
  Resource dropTarget;

  @Test
  void validate_file_creation() throws IOException {
    assertThat(createTarget.getFile()).exists();
    assertThat(dropTarget.getFile()).exists();
  }
}
