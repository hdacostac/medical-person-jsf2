package com.cegedim.web;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.annotation.SessionScope;

import com.cegedim.web.components.CustomMenuComponent;
import com.gvt.web.BaseComponentsConfiguration;
import com.gvt.web.components.MenuComponent;

@Configuration
public class ComponentsConfiguration extends BaseComponentsConfiguration {

	@Bean
	@SessionScope
	@Override
	public MenuComponent menuHandler() {
		return new CustomMenuComponent();
	}
}