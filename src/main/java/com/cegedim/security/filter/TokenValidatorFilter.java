package com.cegedim.security.filter;

import java.io.IOException;
import java.util.Arrays;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.jwt.JwtHelper;
import org.springframework.security.jwt.crypto.sign.MacSigner;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2RestOperations;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.resource.OAuth2ProtectedResourceDetails;
import org.springframework.security.oauth2.client.token.grant.password.ResourceOwnerPasswordResourceDetails;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.web.filter.OncePerRequestFilter;

public class TokenValidatorFilter extends OncePerRequestFilter {

	private static final Logger LOGGER = LoggerFactory.getLogger(TokenValidatorFilter.class);

	private OAuth2ClientContext oAuth2ClientContext;
	private JwtAccessTokenConverter jwtAccessTokenConverter;
	private OAuth2RestOperations myRestTemplate;

	public TokenValidatorFilter(OAuth2ClientContext oAuth2ClientContext,
			JwtAccessTokenConverter jwtAccessTokenConverter, OAuth2RestOperations myRestTemplate) {
		this.oAuth2ClientContext = oAuth2ClientContext;
		this.jwtAccessTokenConverter = jwtAccessTokenConverter;
		this.myRestTemplate = myRestTemplate;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest servletRequest, HttpServletResponse servletResponse,
			FilterChain filterChain) throws ServletException, IOException {
		LOGGER.trace("Entering doFilter on TokenValidatorFilter");

		DefaultOAuth2AccessToken oauthToken = (DefaultOAuth2AccessToken) oAuth2ClientContext.getAccessToken();

		if (oauthToken != null) {
			LOGGER.debug("token:{}", oauthToken.getValue());
			LOGGER.debug("refresh:{}", oauthToken.getRefreshToken());
			LOGGER.debug("refresh:{}", oauthToken.getExpiresIn());
			JwtHelper.decodeAndVerify(oauthToken.getValue(), new MacSigner("Q2VnZWRpbSEx"));

			OAuth2Authentication authentication = (OAuth2Authentication) SecurityContextHolder.getContext()
					.getAuthentication();
			LOGGER.debug("authentication:{}", authentication);

			OAuth2RestTemplate template = new OAuth2RestTemplate(
					fullAccessresourceDetails("http://localhost:8080/oauth/token"), oAuth2ClientContext);

			LOGGER.debug("Access token from template:{}", template.getAccessToken());

			HttpServletRequest request = (HttpServletRequest) servletRequest;
			HttpServletResponse response = (HttpServletResponse) servletResponse;
		}

		filterChain.doFilter(servletRequest, servletResponse);
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
