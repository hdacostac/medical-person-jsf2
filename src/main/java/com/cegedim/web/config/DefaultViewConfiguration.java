package com.cegedim.web.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

import com.gvt.web.config.DefaultWebConfiguration;

@Configuration
public class DefaultViewConfiguration extends DefaultWebConfiguration {

	@Bean
	public MultipartResolver filterMultipartResolver() {
		return new CommonsMultipartResolver();
	}

}