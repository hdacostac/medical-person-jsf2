package com.cegedim.web;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.ClientHttpRequestFactorySupplier;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.oauth2.client.http.OAuth2ErrorResponseErrorHandler;
import org.springframework.security.oauth2.core.http.converter.OAuth2AccessTokenResponseHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import com.gvt.support.rest.handlers.CustomResponseErrorHandler;
import com.gvt.support.rest.handlers.URLRestHandler;
import com.gvt.web.security.converters.CustomTokenResponseConverter;
import com.gvt.web.security.interceptors.LocaleHeaderInterceptor;
import com.gvt.web.security.interceptors.LoggerInterceptor;
import com.gvt.web.security.interceptors.AuthorizationHeaderInterceptor;

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
	@Primary
	public RestTemplate restTemplate(MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter,
			ClientHttpRequestFactory clientHttpRequestFactory, OAuth2ClientContext oAuth2ClientContext) {
		RestTemplate restTemplate = new RestTemplate();

		List<HttpMessageConverter<?>> messageConverters = new ArrayList<>();
		messageConverters.add(new StringHttpMessageConverter());
		messageConverters.add(mappingJackson2HttpMessageConverter);

		restTemplate.setRequestFactory(clientHttpRequestFactory);
		restTemplate.setErrorHandler(new CustomResponseErrorHandler());
		restTemplate.setMessageConverters(messageConverters);
		restTemplate.getInterceptors().add(new LocaleHeaderInterceptor());
		restTemplate.getInterceptors().add(new AuthorizationHeaderInterceptor(oAuth2ClientContext));
		restTemplate.getInterceptors().add(new LoggerInterceptor());

		return restTemplate;
	}

	@Bean
	public RestTemplate oauth2RestTemplate(MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter,
			ClientHttpRequestFactory clientHttpRequestFactory) {
		OAuth2AccessTokenResponseHttpMessageConverter tokenResponseHttpMessageConverter = new OAuth2AccessTokenResponseHttpMessageConverter();
		tokenResponseHttpMessageConverter.setTokenResponseConverter(new CustomTokenResponseConverter());

		List<HttpMessageConverter<?>> messageConverters = new ArrayList<>();
		messageConverters.add(new FormHttpMessageConverter());
		messageConverters.add(new StringHttpMessageConverter());
		messageConverters.add(mappingJackson2HttpMessageConverter);

		return new RestTemplateBuilder().additionalMessageConverters(messageConverters)
				.requestFactory(new ClientHttpRequestFactorySupplier()).interceptors(new LocaleHeaderInterceptor())
				.errorHandler(new OAuth2ErrorResponseErrorHandler()).basicAuthentication("clientIdPassword", "")
				.build();
	}

}