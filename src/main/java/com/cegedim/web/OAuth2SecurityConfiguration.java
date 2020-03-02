package com.cegedim.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.oauth2.client.EnableOAuth2Sso;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.oauth2.client.filter.OAuth2ClientContextFilter;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.context.SecurityContextPersistenceFilter;

import com.gvt.web.security.BaseOAuth2SecurityConfiguration;
import com.gvt.web.security.filters.TokenValidatorFilter;
import com.gvt.web.security.signature.DefaultJWTSecretKeyFactory;
import com.gvt.web.security.signature.JWTSecretKeyFactory;

@Configuration
@EnableWebSecurity
@EnableOAuth2Sso
@EnableGlobalMethodSecurity(prePostEnabled = true)
//@Order(3)
public class OAuth2SecurityConfiguration extends BaseOAuth2SecurityConfiguration {

	@Value("${security.oauth2.client.logoutUri}")
	private String logoutUrl;

	@Autowired
	private OAuth2ClientContextFilter oauth2ClientContextFilter;

	@Value("${security.oauth2.client.accessTokenUri}")
	private String accessTokenUri;

	@Autowired
	private OAuth2ClientContext oAuth2ClientContext;

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.headers().frameOptions().disable().and().cors().and().csrf().disable().authorizeRequests()
				.antMatchers("/login").permitAll().anyRequest().authenticated().and().logout()
				.logoutSuccessUrl(logoutUrl).and().anonymous().disable()
				.addFilterAfter(oauth2ClientContextFilter, SecurityContextPersistenceFilter.class)
				.addFilterAfter(new TokenValidatorFilter(accessTokenUri, oAuth2ClientContext, oauth2RestTemplate),
						BasicAuthenticationFilter.class)
				// this disables session creation on Spring Security
				.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED);
//		.invalidSessionStrategy(new CustomRedirectStrategy("/logout"));

//		http.authorizeRequests().anyRequest().authenticated();
	}

	@Bean
	public JWTSecretKeyFactory jwtSecretKeyFactory(@Value("${app.jwt.secretKey}") String secretKey) {
		return new DefaultJWTSecretKeyFactory(secretKey);
	}
}