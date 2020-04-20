package com.xdev.server.aa.openid.azure;

import com.github.scribejava.core.model.OAuth2AccessToken;
import com.nimbusds.jwt.JWT;

public class AzureToken extends OAuth2AccessToken {

	private static final long serialVersionUID = 1L;
	
	private JWT idToken;
	
	public AzureToken(String accessToken, String tokenType, Integer expiresIn, String refreshToken, String scope,
			String rawResponse, JWT idToken) {
		super(accessToken, tokenType, expiresIn, refreshToken, scope, rawResponse);
		this.idToken = idToken;
	}
	
	
	public AzureToken(String accessToken, String tokenType, Integer expiresIn, String refreshToken, String scope,
			String rawResponse) {
		super(accessToken, tokenType, expiresIn, refreshToken, scope, rawResponse);
	}

	public AzureToken(String accessToken, String rawResponse) {
		super(accessToken, rawResponse);
	}

	public AzureToken(String accessToken) {
		super(accessToken);
	}

	public JWT getIdToken() {
		return this.idToken;
	}


}
