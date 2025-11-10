package com.example.spring.framework.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;


@Configuration
@PropertySource(value = "classpath:application-test.yml", ignoreResourceNotFound = true)
@PropertySource(value = "classpath:test.properties", ignoreResourceNotFound = true)
public class TestConfig {

}
