package org.example.app;

import javax.inject.Inject;
import javax.inject.Named;

import org.example.cdi.Greeter;
import org.springframework.boot.CommandLineRunner;

@Named
public class AppCommandLineRunner implements CommandLineRunner {
	@Inject
	@Named("normalGreeter")
	private Greeter greeter;

	@Override
	public void run(String... args) throws Exception {
		greeter.sayHello();
	}

}
