package com.example.spring.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
// @ComponentScan(basePackages="com.example.spring")
public class AppConfig {
	
	@Bean
	public String appVersion() {
		return "App@1.0";
	}

}
