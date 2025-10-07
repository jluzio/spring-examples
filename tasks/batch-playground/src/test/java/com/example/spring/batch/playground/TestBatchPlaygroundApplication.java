package com.example.spring.batch.playground;

import org.springframework.boot.SpringApplication;

public class TestBatchPlaygroundApplication {

	public static void main(String[] args) {
		SpringApplication.from(BatchPlaygroundApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
