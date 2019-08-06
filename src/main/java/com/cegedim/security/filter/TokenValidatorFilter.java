package com.cegedim.security.filter;

import java.io.IOException;
import java.util.Arrays;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.jwt.JwtHelper;
import org.springframework.security.jwt.crypto.sign.MacSigner;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.oauth2.client.resource.OAuth2ProtectedResourceDetails;
import org.springframework.security.oauth2.client.token.grant.password.ResourceOwnerPasswordResourceDetails;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.ResourceServerTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.filter.OncePerRequestFilter;

public class TokenValidatorFilter extends OncePerRequestFilter {

	private static final Logger LOGGER = LoggerFactory.getLogger(TokenValidatorFilter.class);

	private OAuth2ClientContext oAuth2ClientContext;
	private JwtAccessTokenConverter jwtAccessTokenConverter;
//	private OAuth2RestOperations myRestTemplate;
	private OAuth2AuthorizedClientService clientService;
	private ResourceServerTokenServices resourceServerTokenServices;
	private TokenStore tokenStore;

	public TokenValidatorFilter(OAuth2ClientContext oAuth2ClientContext,
			JwtAccessTokenConverter jwtAccessTokenConverter, OAuth2AuthorizedClientService clientService,
			ResourceServerTokenServices resourceServerTokenServices, TokenStore tokenStore) {
		this.oAuth2ClientContext = oAuth2ClientContext;
		this.jwtAccessTokenConverter = jwtAccessTokenConverter;
//		this.myRestTemplate = myRestTemplate;
		this.clientService = clientService;
		this.resourceServerTokenServices = resourceServerTokenServices;
		this.tokenStore = tokenStore;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest servletRequest, HttpServletResponse servletResponse,
			FilterChain filterChain) throws ServletException, IOException {
		LOGGER.trace("Entering doFilter on TokenValidatorFilter");

		DefaultOAuth2AccessToken oauthToken = (DefaultOAuth2AccessToken) oAuth2ClientContext.getAccessToken();

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

			if (oauthToken.getExpiresIn() < 0) {
				LOGGER.debug("Triyng to expire the session");

//				SecurityContextHolder.clearContext();
//				SecurityContextHolder.getContext().setAuthentication(null);
//				oAuth2ClientContext.setAccessToken(null);
//				tokenStore.removeAccessToken(oauthToken);
////				((ConsumerTokenServices) resourceServerTokenServices).revokeToken(oauthToken.getValue());
//				HttpSession session = servletRequest.getSession(false);
//				session.invalidate();
//				new SecurityContextLogoutHandler().logout(servletRequest, servletResponse, authentication);
				servletResponse.sendRedirect("/person/logout");
				return;
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
