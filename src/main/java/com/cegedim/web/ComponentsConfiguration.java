package com.cegedim.web;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.annotation.SessionScope;

import com.cegedim.web.components.CustomMenuComponent;

import swf.cegedim.web.BaseComponentsConfiguration;
import swf.cegedim.web.components.MenuComponent;

@Configuration
public class ComponentsConfiguration extends BaseComponentsConfiguration {

	@Bean
	@SessionScope
	@Override
	public MenuComponent menuHandler() {
		return new CustomMenuComponent();
	}
}