package com.cegedim.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

public class ServletWebApplication extends SpringBootServletInitializer {

	private static Class<WebApplication> applicationClass = WebApplication.class;

	public static void main(String[] args) {
		SpringApplication.run(applicationClass, args);
	}

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.properties("spring.config.name:person-webapp").sources(applicationClass);
	}

}
