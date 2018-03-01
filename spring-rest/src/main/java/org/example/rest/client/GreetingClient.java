package org.example.rest.client;

import java.util.Collections;

import javax.inject.Inject;
import javax.inject.Named;

import org.example.rest.entity.Greeting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

@Named
public class GreetingClient {
	private Logger log = LoggerFactory.getLogger(getClass());
	@Inject
	private RestTemplate restTemplate;
    private String url = "http://localhost:8080/greeting";  
    
    public GreetingClient() {
	}
    
	public void execute() {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		headers.set("test", "test-val");
		HttpEntity<String> entity = new HttpEntity<>("body", headers);
		
		ResponseEntity<Greeting> greetingEntity = restTemplate.exchange(url, HttpMethod.GET, entity, Greeting.class);
		Greeting greeting = greetingEntity.getBody();
        log.info("Greeting: {}", greeting);
	}

	public void executeSimple() {
		Greeting greeting = restTemplate.getForObject(url, Greeting.class);
        log.info("Greeting: {}", greeting);
	}

}
