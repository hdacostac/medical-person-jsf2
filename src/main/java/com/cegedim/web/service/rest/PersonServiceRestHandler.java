package com.cegedim.web.service.rest;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.cegedim.web.service.PersonService;

import swf.cegedim.rest.handlers.URLRestHandler;

@Component
public class PersonServiceRestHandler implements PersonService {

	private RestTemplate restTemplate;
	private URLRestHandler urlHrRestHandler;

	public PersonServiceRestHandler(RestTemplate restTemplate, URLRestHandler urlHrRestHandler) {
		this.restTemplate = restTemplate;
		this.urlHrRestHandler = urlHrRestHandler;
	}

}
