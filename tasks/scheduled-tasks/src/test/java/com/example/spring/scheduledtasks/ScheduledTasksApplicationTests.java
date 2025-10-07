package com.example.spring.scheduledtasks;

import com.example.spring.scheduledtasks.persistence.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
class ScheduledTasksApplicationTests {

  @Autowired
  private UserRepository userRepository;
	
	@Test
	void contextLoads() {
    // context loaded, all good
	}

}
