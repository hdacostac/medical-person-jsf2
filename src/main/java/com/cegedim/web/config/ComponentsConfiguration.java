package com.cegedim.web.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.annotation.SessionScope;

import com.cegedim.web.components.CustomMenuComponent;
import com.gvt.web.components.MenuComponent;
import com.gvt.web.config.DefaultComponentsConfiguration;

@Configuration
public class ComponentsConfiguration extends DefaultComponentsConfiguration {

	@Bean
	@SessionScope
	@Override
	public MenuComponent menuHandler() {
		return new CustomMenuComponent();
	}
}