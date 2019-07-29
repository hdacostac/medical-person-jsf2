package com.cegedim.security.factory;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;

public class CustomJwtTokenConverter extends JwtAccessTokenConverter {

	private static final Logger LOGGER = LoggerFactory.getLogger(CustomJwtTokenConverter.class);

	@Override
	public OAuth2Authentication extractAuthentication(Map<String, ?> map) {
		LOGGER.info("Executing extractAuthentication");

		OAuth2Authentication auth = super.extractAuthentication(map);

		Map<String, String> parameters = new HashMap<String, String>();
		Set<String> scope = auth.getOAuth2Request().getScope();
		Set<String> resources = auth.getOAuth2Request().getResourceIds();
		String clientId = (String) map.get(CLIENT_ID);
		parameters.put(CLIENT_ID, clientId);
		if (map.containsKey(GRANT_TYPE)) {
			parameters.put(GRANT_TYPE, (String) map.get(GRANT_TYPE));
		}

		Collection<? extends GrantedAuthority> authorities = null;
		if (map.containsKey(AUTHORITIES)) {
			@SuppressWarnings("unchecked")
			String[] roles = ((Collection<String>) map.get(AUTHORITIES)).toArray(new String[0]);
			authorities = AuthorityUtils.createAuthorityList(roles);
		}

		OAuth2Request request = new OAuth2Request(parameters, clientId, authorities, true, scope, resources, null, null,
				null);

		return new OAuth2Authentication(request, auth);
	}

}