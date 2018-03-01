package org.example.rest.controller;

import javax.inject.Inject;

import org.example.rest.client.GreetingClient;
import org.example.rest.client.QuoteClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class InvokeClientController {
	@Inject private GreetingClient greetingClient;
	@Inject private QuoteClient quoteClient;

    @RequestMapping("/invokeClient/{client}")
    public void invokeClient(@PathVariable("client") String clientName) {
    	switch (clientName) {
    	case "greeting":
    		greetingClient.execute();
    		break;
		case "quote":
			quoteClient.execute();
			break;
		}
    }
}