package com.cegedim.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.DispatcherServletAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.ComponentScan;

import com.gvt.web.BaseConfiguration;

@SpringBootApplication(exclude = { DispatcherServletAutoConfiguration.class, ErrorMvcAutoConfiguration.class,
		UserDetailsServiceAutoConfiguration.class })
@ServletComponentScan(basePackages = { "com.gvt", "com.cegedim" })
@ComponentScan(basePackages = { "com.gvt", "com.cegedim" })
public class WebConfiguration extends BaseConfiguration {

	public static void main(String[] args) {
		SpringApplication.run(WebConfiguration.class, args);
	}

}
