package com.cegedim.web;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

import com.gvt.web.security.BaseJWTSecurityConfiguration;

//@Configuration
//@EnableWebSecurity
//@EnableGlobalMethodSecurity(prePostEnabled = true)
public class JWTSecurityConfiguration extends BaseJWTSecurityConfiguration {

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		super.configure(http);

		http.cors().disable().csrf().disable().headers().frameOptions().sameOrigin().and().authorizeRequests()
				.anyRequest().permitAll();
	}

}