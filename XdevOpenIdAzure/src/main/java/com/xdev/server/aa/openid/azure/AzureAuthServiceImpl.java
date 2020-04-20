package com.xdev.server.aa.openid.azure;

import com.github.scribejava.core.builder.api.DefaultApi20;
import com.github.scribejava.core.model.AbstractRequest;
import com.github.scribejava.core.model.OAuthConfig;
import com.github.scribejava.core.oauth.OAuth20Service;

public class AzureAuthServiceImpl extends OAuth20Service {

	public AzureAuthServiceImpl(final DefaultApi20 api, final OAuthConfig config) {
		super(api, config);
	}

	public AzureAuthServiceImpl(final DefaultApi20 api, final OAuthConfig config, final String resource) {
		super(api, config);
	}

	@Override
	protected <T extends AbstractRequest> T createAccessTokenRequest(final String code, final T request) {
		T newRequest = super.createAccessTokenRequest(code, request);
		
		return newRequest;
	}

}
