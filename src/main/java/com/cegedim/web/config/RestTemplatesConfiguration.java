package com.cegedim.web.config;

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
import org.springframework.web.client.RestTemplate;

import com.gvt.http.client.support.AuthorizationHeaderInterceptor;
import com.gvt.http.client.support.LocaleHeaderInterceptor;
import com.gvt.http.client.support.LoggerInterceptor;
import com.gvt.security.oauth2.secret.DefaultJwtSecretKeyFactory;
import com.gvt.security.oauth2.secret.JwtSecretKeyFactory;
import com.gvt.support.rest.handlers.UrlRestHandler;
import com.gvt.web.client.CustomResponseErrorHandler;

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

	@Value("${security.oauth2.client.clientId}")
	private String clientId;

	@Value("${security.oauth2.client.clientSecret}")
	private String clientSecret;

	@Bean
	public UrlRestHandler urlHrRestHandler() {
		UrlRestHandler baseRestHandler = new UrlRestHandler();

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
		messageConverters.add(new FormHttpMessageConverter());
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
//		OAuth2AccessTokenResponseHttpMessageConverter tokenResponseHttpMessageConverter = new OAuth2AccessTokenResponseHttpMessageConverter();
//		tokenResponseHttpMessageConverter.setTokenResponseConverter(new CustomTokenResponseConverter());

		List<HttpMessageConverter<?>> messageConverters = new ArrayList<>();
		messageConverters.add(new FormHttpMessageConverter());
		messageConverters.add(new StringHttpMessageConverter());
		messageConverters.add(mappingJackson2HttpMessageConverter);

		return new RestTemplateBuilder().additionalMessageConverters(messageConverters)
				.requestFactory(new ClientHttpRequestFactorySupplier())
				.interceptors(new LocaleHeaderInterceptor(), new LoggerInterceptor())
				.errorHandler(new OAuth2ErrorResponseErrorHandler()).basicAuthentication(clientId, clientSecret)
				.build();
	}

	@Bean
	public JwtSecretKeyFactory jwtSecretKeyFactory(@Value("${app.jwt.secretKey}") String secretKey) {
		return new DefaultJwtSecretKeyFactory(secretKey);
	}

}