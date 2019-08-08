package com.cegedim.security.filter;

import java.io.IOException;
import java.net.URI;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.jwt.JwtHelper;
import org.springframework.security.jwt.crypto.sign.MacSigner;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.oauth2.client.resource.OAuth2ProtectedResourceDetails;
import org.springframework.security.oauth2.client.token.grant.password.ResourceOwnerPasswordResourceDetails;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;
import org.springframework.security.oauth2.provider.token.ResourceServerTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fasterxml.jackson.core.type.TypeReference;

public class TokenValidatorFilter extends OncePerRequestFilter {

	private static final Logger LOGGER = LoggerFactory.getLogger(TokenValidatorFilter.class);

	private OAuth2ClientContext oAuth2ClientContext;
	private JwtAccessTokenConverter jwtAccessTokenConverter;
//	private OAuth2RestOperations myRestTemplate;
	private OAuth2AuthorizedClientService clientService;
	private ResourceServerTokenServices resourceServerTokenServices;
	private TokenStore tokenStore;
	private RestTemplate oauth2RestTemplate;

	public TokenValidatorFilter(OAuth2ClientContext oAuth2ClientContext,
			JwtAccessTokenConverter jwtAccessTokenConverter, OAuth2AuthorizedClientService clientService,
			ResourceServerTokenServices resourceServerTokenServices, TokenStore tokenStore,
			RestTemplate oauth2RestTemplate) {
		this.oAuth2ClientContext = oAuth2ClientContext;
		this.jwtAccessTokenConverter = jwtAccessTokenConverter;
//		this.myRestTemplate = myRestTemplate;
		this.clientService = clientService;
		this.resourceServerTokenServices = resourceServerTokenServices;
		this.tokenStore = tokenStore;
		this.oauth2RestTemplate = oauth2RestTemplate;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest servletRequest, HttpServletResponse servletResponse,
			FilterChain filterChain) throws ServletException, IOException {
		LOGGER.trace("Entering doFilter on TokenValidatorFilter");

		DefaultOAuth2AccessToken oauthToken = (DefaultOAuth2AccessToken) oAuth2ClientContext.getAccessToken();

		printHeadersAndParams(servletRequest);

		String FACES_REQUEST_HEADER = "faces-request";
		LOGGER.debug("servletRequest.getHeader(FACES_REQUEST_HEADER):{}",
				servletRequest.getHeader(FACES_REQUEST_HEADER));

		if (oauthToken != null) {
			LOGGER.debug("token:{}", oauthToken.getValue());
			LOGGER.debug("refresh:{}", oauthToken.getRefreshToken());
			LOGGER.debug("refresh expires in:{}", oauthToken.getExpiresIn());
			JwtHelper.decodeAndVerify(oauthToken.getValue(), new MacSigner("Q2VnZWRpbSEx"));

			OAuth2Authentication authentication = (OAuth2Authentication) SecurityContextHolder.getContext()
					.getAuthentication();
//			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//			OAuth2AuthenticationToken oauthToken2 = (OAuth2AuthenticationToken) authentication;
			LOGGER.debug("authentication:{}", authentication);
			LOGGER.debug("principal:{}", authentication.getPrincipal());
			LOGGER.debug("authorities:{}", authentication.getAuthorities());
			LOGGER.debug("details:{}", ((OAuth2AuthenticationDetails) authentication.getDetails()).getDecodedDetails());

			if (oauthToken.getExpiresIn() < 10) {
				LOGGER.debug("Triyng to expire the session");

				LinkedMultiValueMap<String, String> refreshBody = new LinkedMultiValueMap();
				refreshBody.add("grant_type", "refresh_token");
				refreshBody.add("refresh_token", oauthToken.getRefreshToken().getValue());

				HttpHeaders headers = new HttpHeaders();
				headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
				headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON_UTF8, MediaType.TEXT_PLAIN));

				try {
					RequestEntity entity = new RequestEntity(refreshBody, headers, HttpMethod.POST,
							new URI("http://localhost:7070/oauth/token"));

					ResponseEntity<String> response = oauth2RestTemplate.exchange(entity, String.class);

					LOGGER.debug("Token:{}", response.getBody());

					if (response.getStatusCode() == HttpStatus.OK) {
						Map<String, Object> map = ((MappingJackson2HttpMessageConverter) oauth2RestTemplate
								.getMessageConverters().get(2)).getObjectMapper().readValue(response.getBody(),
										new TypeReference<Map<String, Object>>() {
										});

						Map<String, String> mapRefreshed = new HashMap<>();
						mapRefreshed.put(OAuth2AccessToken.ACCESS_TOKEN,
								(String) map.get(OAuth2AccessToken.ACCESS_TOKEN));
						mapRefreshed.put(OAuth2AccessToken.EXPIRES_IN,
								String.valueOf((Integer) map.get(OAuth2AccessToken.EXPIRES_IN)));
						mapRefreshed.put(OAuth2AccessToken.REFRESH_TOKEN,
								(String) map.get(OAuth2AccessToken.REFRESH_TOKEN));
						mapRefreshed.put(OAuth2AccessToken.SCOPE, (String) map.get(OAuth2AccessToken.SCOPE));

						OAuth2AccessToken oAuth2AccessTokenRefreshed = DefaultOAuth2AccessToken.valueOf(mapRefreshed);
						oAuth2ClientContext.setAccessToken(oAuth2AccessTokenRefreshed);
					} else {
						redirectLogin(servletRequest, servletResponse);

						return;
					}
				} catch (Exception e) {
					LOGGER.error("Problem with refresh token", e);

					redirectLogin(servletRequest, servletResponse);

					return;
				}

//				redirectLogin(servletRequest, servletResponse);
			}

//			OAuth2RestTemplate template = new OAuth2RestTemplate(
//					fullAccessresourceDetails("http://localhost:8080/oauth/token"), oAuth2ClientContext);
//
//			LOGGER.debug("Access token from template:{}", template.getAccessToken());

//			OAuth2AuthorizedClient client = clientService.loadAuthorizedClient("customOAuth2", "hdacostac@gmail.com");
//			client.getRefreshToken();

			HttpServletRequest request = (HttpServletRequest) servletRequest;
			HttpServletResponse response = (HttpServletResponse) servletResponse;
		}

		filterChain.doFilter(servletRequest, servletResponse);
	}

	private void printHeadersAndParams(HttpServletRequest servletRequest) {
		// print all the headers
		Enumeration headerNames = servletRequest.getHeaderNames();
		while (headerNames.hasMoreElements()) {
			String headerName = (String) headerNames.nextElement();
			logger.info("header: " + headerName + ":" + servletRequest.getHeader(headerName));
		}

		// print all the request params
//		Enumeration params = servletRequest.getParameterNames();
//		while (params.hasMoreElements()) {
//			String paramName = (String) params.nextElement();
//			logger.info("Attribute: '" + paramName + "', Value: '" + servletRequest.getParameter(paramName) + "'");
//		}
	}

	private void redirectLogin(HttpServletRequest servletRequest, HttpServletResponse servletResponse)
			throws IOException {
		if (isAjaxRequest(servletRequest)) {
			String ajaxRedirectXml = createAjaxRedirectXml("/person/logout");
			LOGGER.debug("Ajax partial response to redirect: {}", ajaxRedirectXml);

			servletResponse.setContentType("text/xml");
			servletResponse.getWriter().write(ajaxRedirectXml);
			servletResponse.flushBuffer();
		} else {
			servletResponse.sendRedirect("/person/logout");
		}
	}

	private boolean isAjaxRequest(HttpServletRequest servletRequest) {
		String REQUEST_WITH_HEADER = "X-Requested-With";
		String FACES_REQUEST_HEADER = "faces-request";

		if ("partial/ajax".equalsIgnoreCase(servletRequest.getHeader(FACES_REQUEST_HEADER))
				|| "XMLHttpRequest".equalsIgnoreCase(servletRequest.getHeader(REQUEST_WITH_HEADER))) {
			return true;
		}

		return false;
	}

	private String createAjaxRedirectXml(String redirectUrl) {
		return new StringBuilder().append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>")
				.append("<partial-response><redirect url=\"").append(redirectUrl)
				.append("\"></redirect></partial-response>").toString();
	}

	private OAuth2ProtectedResourceDetails fullAccessresourceDetails(String tokenUrl) {
		ResourceOwnerPasswordResourceDetails resource = new ResourceOwnerPasswordResourceDetails();
		resource.setAccessTokenUri(tokenUrl);
		resource.setClientId("clientIdPassword");
		resource.setGrantType("password");
		resource.setScope(Arrays.asList("read", "write"));
		resource.setUsername("hdacostac@gmail.com");
		resource.setPassword("1234");

		return resource;
	}

}
