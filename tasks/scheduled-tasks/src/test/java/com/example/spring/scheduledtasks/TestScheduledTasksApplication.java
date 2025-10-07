package com.example.spring.scheduledtasks;

import org.springframework.boot.SpringApplication;

public class TestScheduledTasksApplication {

	public static void main(String[] args) {
		SpringApplication.from(ScheduledTasksApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
