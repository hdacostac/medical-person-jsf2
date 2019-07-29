package com.cegedim.security.factory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class SimpleJWTSecretKeyBuilder implements JWTSecretKeyFactory {

	@Autowired
	private Environment env;

	public String getSecretKey() {
		return env.getProperty("app.jwt.secretKey");
	}

}
