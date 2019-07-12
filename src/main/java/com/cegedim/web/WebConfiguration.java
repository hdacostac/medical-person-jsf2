package com.cegedim.web;

import javax.servlet.DispatcherType;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.servlet.DispatcherServletAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

import com.ocpsoft.pretty.PrettyFilter;

import swf.cegedim.web.BaseConfiguration;

@SpringBootApplication(exclude = { DispatcherServletAutoConfiguration.class, ErrorMvcAutoConfiguration.class })
@ServletComponentScan(basePackages = { "swf.cegedim", "com.cegedim" })
@ComponentScan(basePackages = { "swf.cegedim", "com.cegedim" })
public class WebConfiguration extends BaseConfiguration {

	public static void main(String[] args) {
		SpringApplication.run(WebConfiguration.class, args);
	}

	@Bean
	public FilterRegistrationBean<PrettyFilter> prettyFilter() {
		FilterRegistrationBean<PrettyFilter> prettyFilter = new FilterRegistrationBean<>(new PrettyFilter());
		prettyFilter.setDispatcherTypes(DispatcherType.FORWARD, DispatcherType.REQUEST, DispatcherType.ASYNC,
				DispatcherType.ERROR);
		prettyFilter.addUrlPatterns("/*");

		return prettyFilter;
	}

}
