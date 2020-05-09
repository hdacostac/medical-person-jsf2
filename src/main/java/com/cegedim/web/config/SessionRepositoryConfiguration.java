package com.cegedim.web.config;

import java.util.Arrays;

import org.omnifaces.util.Faces;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;
import org.springframework.session.web.http.SessionRepositoryFilter;
import org.springframework.web.filter.DelegatingFilterProxy;

@Configuration
@EnableRedisHttpSession
public class SessionRepositoryConfiguration {

	@Bean
	@Order(value = 0)
	public FilterRegistrationBean sessionRepositoryFilterRegistration(
			SessionRepositoryFilter springSessionRepositoryFilter) {
		FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean();
		filterRegistrationBean.setFilter(new DelegatingFilterProxy(springSessionRepositoryFilter));
		filterRegistrationBean.setUrlPatterns(Arrays.asList("/*"));

		return filterRegistrationBean;
	}

	@Bean
	public JedisConnectionFactory connectionFactory() {
		return new JedisConnectionFactory();
	}

}
