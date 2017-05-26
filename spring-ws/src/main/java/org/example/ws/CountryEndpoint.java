package org.example.ws;

import org.example.ws.model.Country;
import org.example.ws.model.GetCountryRequest;
import org.example.ws.model.GetCountryResponse;
import org.example.ws.model.UpdateCountryRequest;
import org.example.ws.model.UpdateCountryResponse;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

@Endpoint
public class CountryEndpoint {
	public static interface Constants {
		public static final String NAMESPACE_URI = "http://example.org/ws";
	}
	
	@ResponsePayload
	@PayloadRoot(namespace = Constants.NAMESPACE_URI, localPart = "getCountryRequest")
	public GetCountryResponse getCountry(@RequestPayload GetCountryRequest request) {
		Country country = new Country();
		country.setCode(request.getCode());
		country.setName("Country " + request.getCode());
		
		GetCountryResponse response = new GetCountryResponse();
		response.setCountry(country);
		
		return response;
	}
	
	@ResponsePayload
	@PayloadRoot(namespace = Constants.NAMESPACE_URI, localPart = "updateCountryRequest")
	public UpdateCountryResponse updateCountry(@RequestPayload UpdateCountryRequest request) {
		UpdateCountryResponse response = new UpdateCountryResponse();
		response.setCountry(request.getCountry());
		return response;
	}
	
	
}
