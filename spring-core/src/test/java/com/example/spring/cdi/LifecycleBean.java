package com.example.spring.cdi;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class LifecycleBean {
	@PostConstruct
	void init() {
		log.info("init");
	}
	
	@PreDestroy
	void preDestroy() {
		log.info("preDestroy");
	}
}