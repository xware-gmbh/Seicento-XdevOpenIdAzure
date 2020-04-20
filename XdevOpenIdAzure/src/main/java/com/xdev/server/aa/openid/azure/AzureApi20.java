package com.xdev.server.aa.openid.azure;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.scribejava.core.builder.api.DefaultApi20;
import com.github.scribejava.core.extractors.TokenExtractor;
import com.github.scribejava.core.model.OAuth2AccessToken;
import com.github.scribejava.core.model.OAuthConfig;
import com.github.scribejava.core.oauth.OAuth20Service;
import com.xdev.server.aa.openid.helper.ConfigHelper;

public class AzureApi20 extends DefaultApi20 {

	private final static Logger log = LoggerFactory.getLogger(AzureApi20.class);
	
	private final String tokenEndpointSuffix = "token";
	private final String authEndpointSuffix = "authorize";

	private String tenantID;
	private String tokenURL;

	private String graphURL;
	private String managementURL;
	
	private Boolean useAzureVersion2 = false;


	public AzureApi20()
	{
		// Default values
		this.tokenURL = "https://login.microsoftonline.com/";
		this.useAzureVersion2 = false;
		this.tenantID = "common";
		
	}

	@Override
	public String getAccessTokenEndpoint() {
		if (this.tenantID == null)
		{
			this.tenantID = "common";
		}
			
		String tokenBaseUrl = normalize(this.tokenURL)
	                + normalize(this.tenantID)
	                + "oauth2/"
	                + ((useAzureVersion2) ? "v2.0/" : "")
	                + this.tokenEndpointSuffix;
		
		log.debug("TokenBaseUrl: " + tokenBaseUrl);
		
		return tokenBaseUrl;		
	}

	@Override
	protected String getAuthorizationBaseUrl() {
		if (this.tenantID == null)
		{
			this.tenantID = "common";
		}
		String authorizationBaseUrl = normalize(this.tokenURL)
	                + normalize(this.tenantID)
	                + "oauth2/"
	                + ((useAzureVersion2) ? "v2.0/" : "")
	                + this.authEndpointSuffix;
		
		log.debug("AuthorizationBaseUrl: " + authorizationBaseUrl);
		
		return authorizationBaseUrl;
		
	}


    @Override
    public OAuth20Service createService(final OAuthConfig config) {
        return new AzureAuthServiceImpl(this, config);
    }


	@Override
	public TokenExtractor<OAuth2AccessToken> getAccessTokenExtractor() {
		return AzureJsonTokenExtractor.instance();
	}
	
	
	@Override
	public String getAuthorizationUrl(OAuthConfig config, Map<String, String> additionalParams) {
		
		String authURL;
		String nonce = ConfigHelper.getNewNonce();
		
		if (additionalParams != null)
		{
			additionalParams.put(OpenIDConstants.OPENID_NONCE, nonce);
			authURL = super.getAuthorizationUrl(config, additionalParams);
		}
		else
		{
			Map<String, String> params = new HashMap<>();
			params.put(OpenIDConstants.OPENID_NONCE, nonce);
			authURL = super.getAuthorizationUrl(config, params);
		}
		
		log.debug("AuthorizationUrl: " + authURL);
		
		return authURL;
	}	
	
	
	// Getter & Setter

	public String getTenantID()
	{
		return this.tenantID;
	}	

	public AzureApi20 withTenantID(final String tenantID)
	{
		this.tenantID = tenantID;
		return this;
	}
	
	public Boolean isAzureV2() {
		return this.useAzureVersion2;
	}

	public AzureApi20 withAzureV2(final boolean useV2) {
		this.useAzureVersion2 = useV2;
		return this;
	}	

	public String getTokenURL() {
		return this.tokenURL;
	}

	public AzureApi20 withTokenURL(final String tokenURL) {
		this.tokenURL = tokenURL;
		return this;
	}

	public String getGraphURL() {
		return this.graphURL;
	}

	public AzureApi20 withGraphURL(final String graphURL) {
		this.graphURL = graphURL;
		return this;
	}

	public String getManagementURL() {
		return this.managementURL;
	}

	public AzureApi20 withManagementURL(final String managementURL) {
		this.managementURL = managementURL;
		return this;
	}

	
	// Internal helper
	
	private String normalize(final String input)
	{
		if (input == null || input.isEmpty())
		{
			return "";
		}
		if (input.endsWith("/"))
		{
			return input;
		}
		else
		{
			return input + "/";
		}
	}

}
