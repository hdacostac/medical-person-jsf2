package com.cegedim.web;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.oauth2.client.EnableOAuth2Sso;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

import com.gvt.web.security.BaseOAuth2SecurityConfiguration;
import com.gvt.web.security.signature.DefaultJWTSecretKeyFactory;
import com.gvt.web.security.signature.JWTSecretKeyFactory;

@Configuration
@EnableWebSecurity
@EnableOAuth2Sso
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class OAuth2SecurityConfiguration extends BaseOAuth2SecurityConfiguration {

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		super.configure(http);

		http.authorizeRequests().anyRequest().authenticated();
	}

	@Bean
	public JWTSecretKeyFactory jwtSecretKeyFactory(@Value("${app.jwt.secretKey}") String secretKey) {
		return new DefaultJWTSecretKeyFactory(secretKey);
	}

}
