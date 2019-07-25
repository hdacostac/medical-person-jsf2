package com.cegedim.web;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import com.gvt.support.rest.handlers.CustomResponseErrorHandler;
import com.gvt.support.rest.handlers.URLRestHandler;
import com.gvt.web.security.interceptors.OAuth2AuthorizationInterceptor;

import swf.cegedim.web.interceptors.HeaderInterceptor;

@Configuration
public class RestTemplatesConfiguration {

	@Value("${app.rest.port}")
	protected String restPort;

	@Value("${app.rest.host}")
	protected String restHost;

	@Value("${app.rest.scheme}")
	protected String restScheme;

	@Value("${app.rest.context}")
	protected String restContext;

	@Bean
	public URLRestHandler urlHrRestHandler() {
		URLRestHandler baseRestHandler = new URLRestHandler();

		baseRestHandler.setRestContext(restContext);
		baseRestHandler.setRestHost(restHost);
		baseRestHandler.setRestPort(restPort);
		baseRestHandler.setRestScheme(restScheme);

		return baseRestHandler;
	}

	@Bean
	public RestTemplate restTemplate(MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter,
			ClientHttpRequestFactory clientHttpRequestFactory) {
		RestTemplate restTemplate = new RestTemplate();

		List<HttpMessageConverter<?>> messageConverters = new ArrayList<>();
		messageConverters.add(new StringHttpMessageConverter());
		messageConverters.add(mappingJackson2HttpMessageConverter);

		restTemplate.setRequestFactory(clientHttpRequestFactory);
		restTemplate.setErrorHandler(new CustomResponseErrorHandler());
		restTemplate.setMessageConverters(messageConverters);
		restTemplate.getInterceptors().add(new HeaderInterceptor());
		restTemplate.getInterceptors().add(new OAuth2AuthorizationInterceptor());

		return restTemplate;
	}

}