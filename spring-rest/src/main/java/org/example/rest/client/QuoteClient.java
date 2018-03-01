package org.example.rest.client;

import javax.inject.Inject;
import javax.inject.Named;

import org.example.rest.entity.Quote;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestTemplate;

@Named
public class QuoteClient {
	private Logger log = LoggerFactory.getLogger(getClass());
    @Inject
    private RestTemplate restTemplate;
    
    public QuoteClient() {
	}
    
	public void execute() {
        Quote quote = restTemplate.getForObject("http://gturnquist-quoters.cfapps.io/api/random", Quote.class);
        log.info(quote.toString());
	}

}
