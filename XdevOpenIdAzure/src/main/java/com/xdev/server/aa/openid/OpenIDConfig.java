package com.xdev.server.aa.openid;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Properties;

import com.nimbusds.jose.Algorithm;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.oauth2.sdk.id.ClientID;
import com.nimbusds.oauth2.sdk.id.Issuer;
import com.nimbusds.openid.connect.sdk.op.OIDCProviderMetadata;
import com.nimbusds.openid.connect.sdk.validators.IDTokenValidator;

public class OpenIDConfig {

    private final String tenantid;
    private final String clientid;
    private String clientkey;
    private String callbackURL; 
    private OIDCProviderMetadata discoveryResult;

	private Properties rawProperties;
    
	private final String tokenBaseURL;
    
    
    public OpenIDConfig(String tenantid, String clientid, String clientkey, String tokenBaseURL) {
		super();
		this.tenantid = tenantid;
		this.clientid = clientid;
		this.clientkey = clientkey;
		this.tokenBaseURL = tokenBaseURL;
	}


	public String getCallbackURL() {
		return callbackURL;
	}


	public void setCallbackURL(String callbackURL) {
		this.callbackURL = callbackURL;
	}


	public String getTenantid() {
		return tenantid;
	}


	public String getClientid() {
		return clientid;
	}


	public String getClientkey() {
		return clientkey;
	}
	
    protected void setClientkey(String clientkey) {
		this.clientkey = clientkey;
	}


	public String getTokenBaseURL() {
		return tokenBaseURL;
	}


	public OIDCProviderMetadata getDiscoveryResult() {
		return discoveryResult;
	}


	public void setDiscoveryResult(OIDCProviderMetadata discoveryResult) {
		this.discoveryResult = discoveryResult;
	}
	
    public Properties getRawProperties() {
		return rawProperties;
	}


	public void setRawProperties(Properties rawProperties) {
		this.rawProperties = rawProperties;
	}


	public IDTokenValidator getValidator(Algorithm alg) throws MalformedURLException {
    	
    	if (alg == null) return null;
    	
    	JWSAlgorithm currentAlg = JWSAlgorithm.parse(alg.getName());

    	if (discoveryResult != null)
    	{
    		URL jwkseturi = discoveryResult.getJWKSetURI().toURL();
    		Issuer issuer = discoveryResult.getIssuer();
    		List<JWSAlgorithm> algList = discoveryResult.getIDTokenJWSAlgs();
    		
    		if (algList != null && algList.contains(currentAlg))
    		{
    			return new IDTokenValidator(issuer, new ClientID(clientid), currentAlg, jwkseturi);
    		}
    	}
    	return null;
    }


}
