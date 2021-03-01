package com.example.spring.core.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;


@Configuration
@PropertySources({
    @PropertySource(value = "classpath:application-test.yml", ignoreResourceNotFound = true),
    @PropertySource(value = "classpath:test.properties", ignoreResourceNotFound = true)
})
public class TestConfig {

}
