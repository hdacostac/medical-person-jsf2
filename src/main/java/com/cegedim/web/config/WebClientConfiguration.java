package com.cegedim.web.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ClientHttpConnector;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.ExchangeFunction;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

import com.gvt.core.security.SecurityConstants;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

@Configuration
public class WebClientConfiguration {

	private static final Logger logger = LoggerFactory.getLogger(WebClientConfiguration.class);

	@Value("${app.rest.port}")
	protected String restPort;

	@Value("${app.rest.host}")
	protected String restHost;

	@Value("${app.rest.scheme}")
	protected String restScheme;

	@Value("${app.rest.context}")
	protected String restContext;

	@Autowired
	private OAuth2ClientContext oAuth2ClientContext;

	@Bean
	public WebClient getWebClient() {
		HttpClient httpClient = HttpClient.create().tcpConfiguration(
				client -> client.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10000).doOnConnected(conn -> conn
						.addHandlerLast(new ReadTimeoutHandler(10)).addHandlerLast(new WriteTimeoutHandler(10))));

		ClientHttpConnector connector = new ReactorClientHttpConnector(httpClient);

		return WebClient.builder()
				.baseUrl(UriComponentsBuilder.newInstance().scheme(restScheme).host(restHost).port(restPort)
						.path(restContext).build().toUriString())
				.clientConnector(connector).defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
				.filter(new AddHeaderExchangeFilter()).filter(logRequest()).filter(logResponse()).build();
	}

	class AddHeaderExchangeFilter implements ExchangeFilterFunction {
		@Override
		public Mono<ClientResponse> filter(ClientRequest request, ExchangeFunction next) {
			if (oAuth2ClientContext != null && oAuth2ClientContext.getAccessToken() != null) {
				logger.trace("Adding {} header to webclient", SecurityConstants.HEADER_AUTHORIZATION);
				logger.trace("\tUsing {} oAuth2ClientContext", oAuth2ClientContext);
				logger.trace("\tUsing {} oAuth2ClientContext.getAccessToken()", oAuth2ClientContext.getAccessToken());

				ClientRequest newRequest = ClientRequest.from(request)
						.header(SecurityConstants.HEADER_AUTHORIZATION,
								SecurityConstants.TOKEN_PREFIX
										+ ((DefaultOAuth2AccessToken) oAuth2ClientContext.getAccessToken()).getValue())
						.build();

				return next.exchange(newRequest);
			}

			return next.exchange(request);
		}
	}

	private ExchangeFilterFunction authorizationHeader() {
		return (clientRequest, next) -> {
			if (oAuth2ClientContext != null && oAuth2ClientContext.getAccessToken() != null) {
				logger.trace("Adding {} header to webclient", SecurityConstants.HEADER_AUTHORIZATION);
				logger.trace("\tUsing {} oAuth2ClientContext", oAuth2ClientContext);
				logger.trace("\tUsing {} oAuth2ClientContext.getAccessToken()", oAuth2ClientContext.getAccessToken());

				clientRequest.headers().add(SecurityConstants.HEADER_AUTHORIZATION, SecurityConstants.TOKEN_PREFIX
						+ ((DefaultOAuth2AccessToken) oAuth2ClientContext.getAccessToken()).getValue());
			}

			return next.exchange(clientRequest);
		};
	}

	private ExchangeFilterFunction logRequest() {
		return ExchangeFilterFunction.ofRequestProcessor(clientRequest -> {
			logger.info("WebClient request: {} {} {}", clientRequest.method(), clientRequest.url(),
					clientRequest.body());
			clientRequest.headers()
					.forEach((name, values) -> values.forEach(value -> logger.info("{}={}", name, value)));

			return Mono.just(clientRequest);
		});
	}

	private ExchangeFilterFunction logResponse() {
		return ExchangeFilterFunction.ofResponseProcessor(clientResponse -> {
			logger.info("WebClient response status: {}", clientResponse.statusCode());

			return Mono.just(clientResponse);
		});
	}

}