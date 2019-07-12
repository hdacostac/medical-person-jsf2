package com.cegedim.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

public class ServletJSF2WebConfiguration extends SpringBootServletInitializer {

	private static Class<WebConfiguration> applicationClass = WebConfiguration.class;

	public static void main(String[] args) {
		SpringApplication.run(applicationClass, args);
	}

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.properties("spring.config.name:person-webapp").sources(applicationClass);
	}

}
