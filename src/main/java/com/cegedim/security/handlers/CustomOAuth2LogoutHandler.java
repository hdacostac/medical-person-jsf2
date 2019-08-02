package com.cegedim.security.handlers;

import java.util.Arrays;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Component
public class CustomOAuth2LogoutHandler implements LogoutHandler {

	private static final Logger LOGGER = LoggerFactory.getLogger(CustomOAuth2LogoutHandler.class);

//	@Value("http://localhost:8080/token")
	String logoutUrl = "http://localhost:8080/token";

	@Override
	public void logout(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
			Authentication authentication) {
		LOGGER.debug("executing MySsoLogoutHandler.logout");

		Object details = authentication.getDetails();
		if (details.getClass().isAssignableFrom(OAuth2AuthenticationDetails.class)) {
			String accessToken = ((OAuth2AuthenticationDetails) details).getTokenValue();
			LOGGER.debug("token: {}", accessToken);

			RestTemplate restTemplate = new RestTemplate();

			MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
			params.add("access_token", accessToken);

			HttpHeaders headers = new HttpHeaders();
			headers.add("Authorization", "Bearer " + accessToken);

			HttpEntity<String> request = new HttpEntity(params, headers);

			HttpMessageConverter formHttpMessageConverter = new FormHttpMessageConverter();
			HttpMessageConverter stringHttpMessageConverternew = new StringHttpMessageConverter();
			restTemplate.setMessageConverters(Arrays
					.asList(new HttpMessageConverter[] { formHttpMessageConverter, stringHttpMessageConverternew }));

			try {
				ResponseEntity<String> response = restTemplate.exchange(logoutUrl, HttpMethod.DELETE, request,
						String.class);

				if (response.getStatusCode() == HttpStatus.OK) {
					LOGGER.debug("Token revoked in Authorization Server successfuly");

//					throw new AuthenticationCredentialsNotFoundException(
//							"An Authentication object was not found in the SecurityContext");

					Cookie[] cookies = httpServletRequest.getCookies();
					if (cookies != null) {
						for (Cookie cookie : cookies) {
//							cookie.setValue("");
//							cookie.setPath("/");
							cookie.setMaxAge(0);
							httpServletResponse.addCookie(cookie);
						}
					}

					Cookie cookieWithSlash = new Cookie("JSESSIONID", null);
					// Tomcat adds extra slash at the end of context path (e.g. "/foo/")
					cookieWithSlash.setPath(httpServletRequest.getContextPath() + "/");
					cookieWithSlash.setMaxAge(0);

					Cookie cookieWithoutSlash = new Cookie("JSESSIONID", null);
					// JBoss doesn't add extra slash at the end of context path (e.g. "/foo")
					cookieWithoutSlash.setPath(httpServletRequest.getContextPath());
					cookieWithoutSlash.setMaxAge(0);

					// Remove cookies on logout so that invalidSessionURL (session timeout) is not
					// displayed on proper logout event
					httpServletResponse.addCookie(cookieWithSlash); // For Tomcat
					httpServletResponse.addCookie(cookieWithoutSlash); // For JBoss

				}
			} catch (HttpClientErrorException e) {
				LOGGER.error(
						"HttpClientErrorException invalidating token with SSO authorization server. response.status code: {}, server URL: {}",
						e.getStatusCode(), logoutUrl);
			}
		}
	}
}
