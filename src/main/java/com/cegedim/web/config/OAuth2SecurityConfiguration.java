package com.cegedim.web.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.oauth2.client.EnableOAuth2Sso;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

import com.gvt.security.config.DefaultOAuth2SecurityConfiguration;
import com.gvt.security.oauth2.secret.DefaultJwtSecretKeyFactory;
import com.gvt.security.oauth2.secret.JwtSecretKeyFactory;

@Configuration
@EnableWebSecurity
@EnableOAuth2Sso
@EnableGlobalMethodSecurity(prePostEnabled = true)
//@Order(3)
public class OAuth2SecurityConfiguration extends DefaultOAuth2SecurityConfiguration {

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		super.configure(http);

//		http.authorizeRequests().anyRequest().authenticated();
	}

	
}