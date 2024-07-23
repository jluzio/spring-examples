package com.example.spring.messaging.kafka;

import java.util.function.Function;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class KafkaApplication {

	public static void main(String[] args) {
		SpringApplication.run(KafkaApplication.class, args);
	}

	@Bean
	public Function<String, String> uppercase() {
		return value -> {
			System.out.println("Received: " + value);
			return value.toUpperCase();
		};
	}

}
