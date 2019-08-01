package com.cegedim.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.oauth2.client.EnableOAuth2Sso;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.oauth2.client.filter.OAuth2ClientContextFilter;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationManager;
import org.springframework.security.oauth2.provider.token.RemoteTokenServices;
import org.springframework.security.oauth2.provider.token.ResourceServerTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.context.SecurityContextPersistenceFilter;

import com.cegedim.security.factory.CustomJwtTokenConverter;
import com.cegedim.security.factory.JWTSecretKeyFactory;
import com.cegedim.security.filter.TokenValidatorFilter;

@Configuration
@EnableWebSecurity
@EnableOAuth2Sso
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class BaseOAuth2SecurityConfiguration extends WebSecurityConfigurerAdapter {

//	private static final String[] AUTH_WHITELIST = {
//			// -- swagger ui
//			"/v2/api-docs", "/configuration/**", "/swagger-resources/**", "/swagger-ui.html", "/webjars/**",
//			"/api-docs/**", "/performance-monitor/**" };

//	@Autowired
//	private ConfigParameters webConfigParameters;

	@Autowired
	private OAuth2ClientContextFilter oauth2ClientContextFilter;

	@Value("${security.oauth2.client.checkTokenUri}")
	private String checkTokenEndpointUrl;

	@Value("${security.oauth2.client.clientId}")
	private String clientId;

	@Value("${security.oauth2.client.clientSecret}")
	private String clientSecret;

//	@Override
//	public void configure(WebSecurity web) throws Exception {
//		web.ignoring().antMatchers(AUTH_WHITELIST).antMatchers("/javax.faces.resource/**",
//				webConfigParameters.getSpringDispatcherMapping() + "/javax.faces.resource/**",
//				webConfigParameters.getSpringDispatcherMapping() + "/api/monitor/**",
//				webConfigParameters.getSpringDispatcherMapping() + "/lifecheck");
//	}

//	@Bean
//	public FilterRegistrationBean<OAuth2ClientContextFilter> oauth2ClientFilterRegistration(
//			OAuth2ClientContextFilter filter) {
//		FilterRegistrationBean<OAuth2ClientContextFilter> registration = new FilterRegistrationBean<>();
//		registration.setFilter(filter);
//		registration.setOrder(-100);
//
//		return registration;
//	}

	@Autowired
	private OAuth2ClientContext oAuth2ClientContext;

//	@Autowired
//	private OAuth2RestOperations myRestTemplate;

	@Autowired
	private OAuth2AuthorizedClientService clientService;

	@Bean
	public ClientRegistrationRepository clientRegistrationRepository() {
		return new InMemoryClientRegistrationRepository(this.myWebsiteClientRegistration());
	}

	private ClientRegistration myWebsiteClientRegistration() {
		return ClientRegistration.withRegistrationId("customOAuth2")
				.authorizationUri("http://localhost:8080/oauth/authorize").clientId("clientIdPassword").clientSecret("")
				.tokenUri("http://localhost:8080/oauth/token")
				.authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
				.redirectUriTemplate("http://localhost:8081/person").build();
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.headers().frameOptions().disable().and().cors().and().csrf().disable().authorizeRequests().anyRequest()
				.authenticated().and().logout().permitAll().and()
				.addFilterAfter(oauth2ClientContextFilter, SecurityContextPersistenceFilter.class)
				.addFilterAfter(new TokenValidatorFilter(oAuth2ClientContext, customAccessTokenConverter(),
						clientService, userInfoTokenServices(), tokenStore()), BasicAuthenticationFilter.class)
				// this disables session creation on Spring Security
				.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED);
	}

//	@Bean
//	public AccessTokenConverter accessTokenConverter() {
//		return new JwtAccessTokenConverter();
//	}

	@Autowired
	private JWTSecretKeyFactory jwtSecretKeyFactory;

	@Bean
	public TokenStore tokenStore() {
		return new JwtTokenStore(customAccessTokenConverter());
	}

	@Bean
	public JwtAccessTokenConverter customAccessTokenConverter() {
		JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
		converter.setAccessTokenConverter(new CustomJwtTokenConverter());

		return converter;
	}

	@Bean
	public ResourceServerTokenServices userInfoTokenServices() {
		final RemoteTokenServices remoteTokenServices = new RemoteTokenServices();
		remoteTokenServices.setCheckTokenEndpointUrl(checkTokenEndpointUrl);
		remoteTokenServices.setClientId(clientId);
		remoteTokenServices.setClientSecret(clientSecret);
		remoteTokenServices.setAccessTokenConverter(customAccessTokenConverter());

		return remoteTokenServices;
	}

	@Override
	@Bean
	public AuthenticationManager authenticationManager() throws Exception {
		OAuth2AuthenticationManager authenticationManager = new OAuth2AuthenticationManager();
		authenticationManager.setTokenServices(userInfoTokenServices());

		return authenticationManager;
	}

}
