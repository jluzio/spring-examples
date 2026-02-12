package com.example.tools.flyway;


import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase.Replace;
import org.springframework.core.io.Resource;

// NOTE: can run with different profiles to generate SQL for other drivers
@DataJpaTest(properties = "spring.profiles.default: h2-mem,jpa-gen-scripts,flyway-disabled")
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Slf4j
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
