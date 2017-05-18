package org.example.cdi;

import org.springframework.stereotype.Component;

@Component("theFancyGreeter")
public class FancyGreeter implements Greeter {

	@Override
	public void sayHello() {
		System.out.println("Hello sir!");
	}

}
