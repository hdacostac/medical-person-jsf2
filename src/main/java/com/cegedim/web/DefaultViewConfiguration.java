package com.cegedim.web;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

import com.gvt.web.BaseDefaultViewConfiguration;

@Configuration
public class DefaultViewConfiguration extends BaseDefaultViewConfiguration {

	@Bean
	public CommonsMultipartResolver filterMultipartResolver() {
		return new CommonsMultipartResolver();
	}

}