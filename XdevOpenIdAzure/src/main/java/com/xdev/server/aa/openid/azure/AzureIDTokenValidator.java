package com.xdev.server.aa.openid.azure;

import java.text.ParseException;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.oauth2.sdk.id.Audience;
import com.nimbusds.openid.connect.sdk.Nonce;
import com.nimbusds.openid.connect.sdk.claims.IDTokenClaimsSet;
import com.nimbusds.openid.connect.sdk.validators.IDTokenValidator;
import com.vaadin.server.VaadinServlet;
import com.xdev.server.aa.openid.OpenIDConfig;
import com.xdev.server.aa.openid.TokenValidationException;
import com.xdev.server.aa.openid.helper.ConfigHelper;
import com.xdev.server.aa.openid.helper.DiscoveryHelper;
import com.xdev.server.aa.openid.helper.StoreHelper;

public class AzureIDTokenValidator {

	private static final Logger log = LoggerFactory.getLogger(AzureIDTokenValidator.class);
	
	public AzureIDTokenValidator() {
	}

	public void validateAzureIDToken(AzureToken token) throws TokenValidationException
	{
		if (token == null || token.getIdToken() == null)
		{
			throw new TokenValidationException("There is no ID token");
		}
		validateIDToken(token.getIdToken());
	}

	protected void validateIDToken(JWT token) throws TokenValidationException
	{
		AzureBaseConfig config;
		JWTClaimsSet jwtClaimsSet;
		String nonce;
		try {
			// See
			// http://openid.net/specs/openid-connect-core-1_0.html#IDTokenValidation

			config = ConfigHelper.getAzureBaseConfig(VaadinServlet.getCurrent().getServletContext());
		
			jwtClaimsSet = token.getJWTClaimsSet();
			
			nonce = jwtClaimsSet.getStringClaim("nonce");
		}
		catch (ParseException e) {
			throw new TokenValidationException("Necessary claim for validation could not be extracted.", e);
		}
		catch (Exception e) {
			throw new TokenValidationException("For the moment");
		}
		
		IDTokenClaimsSet validatedJwtClaimsSet = validateIssuerAndSignature(token, config);
		if (validatedJwtClaimsSet != null)
		{
			validateAudience(validatedJwtClaimsSet, config);
			validateTime(validatedJwtClaimsSet);
		}
		else
		{
			validateAudience(jwtClaimsSet, config);
			validateTime(jwtClaimsSet);
		}


		validateNonce(nonce);


	}

	/**
	 * Validates only if there is a OIDCProviderMetadata in the
	 * {@link OpenIDConfig} see {@link DiscoveryHelper} performDiscovery
	 * 
	 * @param token
	 * @throws Exception
	 */
	private IDTokenClaimsSet validateIssuerAndSignature(JWT token, AzureBaseConfig config) throws TokenValidationException {
		try {
			IDTokenValidator validator = config.getValidator(token.getHeader().getAlgorithm());
			if (validator != null) {
				return validator.validate(token, new Nonce(ConfigHelper.getCurrentNonce().orElse("")));
			}
			
			log.warn("No IDTokenValidator is set! Use DiscoveryHelper.performDiscovery() to fetch endpoint's openid config!");
			
			return null;
		} catch (Exception e) {
			throw new TokenValidationException("Error at validation issuer and signature", e);
		}
	}

	protected void validateNonce(String returnedNonce) throws TokenValidationException {

		// http://openid.net/specs/openid-connect-core-1_0.html#IDTokenValidation
		// - No.11
		if (StringUtils.isEmpty(returnedNonce) || !returnedNonce.equals(ConfigHelper.getCurrentNonce().orElse(""))) {
			throw new TokenValidationException("Token nonce does not match the application's nonce");
		}
	}

	protected void validate(String original, String returnedValue) throws Exception {
		if (StringUtils.isEmpty(returnedValue) || !returnedValue.equals(original)) {
			throw new Exception("Values doesn't match");
		}
	}

	protected void validateAudience(JWTClaimsSet claimsSet, AzureBaseConfig conf) throws TokenValidationException {

		// http://openid.net/specs/openid-connect-core-1_0.html#IDTokenValidation

		if (claimsSet.getAudience() == null) {
			throw new TokenValidationException("Audience does not exist.");
		}
		if (claimsSet.getAudience().isEmpty() || !claimsSet.getAudience().contains(conf.getClientid())) {
			throw new TokenValidationException("Client ID is not in token's audience.");
		}
		if (claimsSet.getAudience().size() > 1) {
			try {
				claimsSet.getStringClaim("azp");
				if (!claimsSet.getStringClaim("azp").equals(conf.getClientid()))
				{
					throw new TokenValidationException("The \"azp\" claim value is not the client id");
				}
			} catch (ParseException e) {
				throw new TokenValidationException("Multiple audience entries, but no \"azp\" claim.", e);
			}
		}
	}
	
	protected void validateAudience(IDTokenClaimsSet claimsSet, AzureBaseConfig conf) throws TokenValidationException {

		// http://openid.net/specs/openid-connect-core-1_0.html#IDTokenValidation

		if (claimsSet.getAudience() == null) {
			throw new TokenValidationException("Audience does not exist.");
		}
		if (claimsSet.getAudience().isEmpty() || !claimsSet.getAudience().contains(new Audience(conf.getClientid()))) {
			throw new TokenValidationException("Client ID is not in token's audience.");
		}
		if (claimsSet.getAudience().size() > 1) {
			if (claimsSet.getStringClaim("azp") == null)
			{
				throw new TokenValidationException("Multiple audience entries, but no \"azp\" claim.");
			}
			if (!claimsSet.getStringClaim("azp").equals(conf.getClientid()))
			{
				throw new TokenValidationException("The \"azp\" claim value is not the client id");
			}
		}
	}	

	protected void validateTime(IDTokenClaimsSet jwtClaimsSet) throws TokenValidationException {

		if (!jwtClaimsSet.getExpirationTime().after(new Date())) {
			throw new TokenValidationException("Current time is not before the time represented by the \"exp\" claim");
		}

	}	
	
	protected void validateTime(JWTClaimsSet jwtClaimsSet) throws TokenValidationException {

		if (!jwtClaimsSet.getExpirationTime().after(new Date())) {
			throw new TokenValidationException("Current time is not before the time represented by the \"exp\" claim");
		}

	}

	protected AzurePopupConfig getStoredConfigData() {
		AzurePopupConfig config = (AzurePopupConfig) StoreHelper.getParamFromContextStore(
				VaadinServlet.getCurrent().getServletContext(), AzurePopupConfig.CONTEXT_CONFIG_PROPERTY);
		return config;
	}	
	
}
