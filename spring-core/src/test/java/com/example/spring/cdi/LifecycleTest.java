package com.example.spring.cdi;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import lombok.extern.slf4j.Slf4j;

@SpringBootTest
@Slf4j
public class LifecycleTest {
	@Autowired
	private LifecycleBean lifecycleBean;
	
	@Test
	public void test() {
		log.info("foo {}", lifecycleBean);
	}

}
