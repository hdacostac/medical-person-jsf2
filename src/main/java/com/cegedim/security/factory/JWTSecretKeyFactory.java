package com.cegedim.security.factory;

/**
 * This interface is implemented by objects that knows how to retrieve the
 * secret key of our API REST modules authorization.
 *
 */
public interface JWTSecretKeyFactory {
	/**
	 * This method will return the actual secret key, to validate the JWT signature.
	 * 
	 * @return secretKey
	 */
	public String getSecretKey();
}
