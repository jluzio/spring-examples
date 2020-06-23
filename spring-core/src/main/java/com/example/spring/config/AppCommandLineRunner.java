package com.example.spring.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.example.spring.cdi.Greeter;

@Component
public class AppCommandLineRunner implements CommandLineRunner {
	@Autowired
	@Qualifier("normalGreeter")
	private Greeter greeter;

	@Override
	public void run(String... args) throws Exception {
		greeter.sayHello();
	}

}
