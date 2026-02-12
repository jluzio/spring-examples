package com.example.tools.flyway;


import lombok.extern.slf4j.Slf4j;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.MigrationInfoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@Slf4j
//@ActiveProfiles({"h2-mem", "jpa-gen-scripts"})
@ActiveProfiles({"h2-mem"})
class FlywayApiTest {

  @Autowired
  Flyway flyway;

  @Test
  void info() {
    MigrationInfoService info = flyway.info();
    log.info("info: {}", info);
  }

}
