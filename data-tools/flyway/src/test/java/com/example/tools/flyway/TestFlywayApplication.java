package com.example.tools.flyway;

import org.springframework.boot.SpringApplication;

public class TestFlywayApplication {

	public static void main(String[] args) {
		SpringApplication.from(FlywayApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
