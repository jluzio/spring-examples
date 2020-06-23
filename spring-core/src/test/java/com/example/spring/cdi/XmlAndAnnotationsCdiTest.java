package com.example.spring.cdi;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

import lombok.extern.slf4j.Slf4j;

@SpringBootTest
@Slf4j
public class XmlAndAnnotationsCdiTest {
	@Autowired
	private GreeterManager greeterManager;
	@Autowired
	private String appVersion;
	
	@Configuration
	@ComponentScan(basePackages="com.example.spring")
	@ImportResource("classpath:/com/example/spring/cdi/XmlAndAnnotationsCdiTest.applicationContext.xml")
	static class Config {
	}

    @Test
    public void exampleTest() {
    	log.info("appVersion: {}", appVersion);
    	log.info("greeterManager: {}", greeterManager);
    	greeterManager.greet();
    	greeterManager.fancyGreet();
    }

}