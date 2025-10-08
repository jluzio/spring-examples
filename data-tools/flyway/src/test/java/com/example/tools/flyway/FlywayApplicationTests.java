package com.example.tools.flyway;

import com.example.tools.flyway.persistence.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
class FlywayApplicationTests {

  @Autowired
  private UserRepository userRepository;
	
	@Test
	void contextLoads() {
    // context loaded, all good
	}

}
