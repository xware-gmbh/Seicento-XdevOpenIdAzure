package com.xdev.server.aa.openid.auth;

import java.io.Serializable;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTClaimsSet;
import com.xdev.security.authentication.ui.Authentication;
import com.xdev.security.authorization.Role;
import com.xdev.security.authorization.Subject;
import com.xdev.server.aa.openid.azure.AzureToken;


/**
 * Implementation for a {@link Subject} wrapped around a Azure ID Token.
 *
 * @author XDEV Software (JM)
 *
 */
public class AzureUser implements Subject, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7932693413085944575L;
	private final String						oid;
	private volatile Set<Role>					roles;
	private JWT                                 rawIDToken; 
	
	/**
	 * Creates a new Azure User with a OpenID token (JSON Web Token (JWT).
	 * 
	 * @param idToken
	 * @throws Exception
	 */
	public AzureUser(final JWT idToken) throws Exception
	{
		super();
		this.rawIDToken = idToken;
		try {
			this.oid = idToken.getJWTClaimsSet().getStringClaim("oid");
		}
		catch (Exception e) {
			throw new Exception("Could not extract identifier from id token", e);
		}
	}		
	
	
	@Override
	public String name() {
		try {
			return getIDToken().getJWTClaimsSet().getStringClaim("name");
		} catch (Exception e) {
			return oid;
		}
	}
	
	public String getIdentifier() {
		return this.oid;
	}
	

	public String getUniqueName() {
		try {
			return getIDToken().getJWTClaimsSet().getStringClaim("unique_name");
		} catch (Exception e) {
			return oid;
		}
	}

	@Override
	public Set<Role> roles() {
		return this.roles;
	}
	

	public JWT getIDToken()
	{
		return rawIDToken;
	}
	
	public String getAccessToken()
	{
		Object token = Authentication.getAuthenticationResult();
		if (token != null && token instanceof AzureToken)
		{
			return ((AzureToken) token).getAccessToken();
		}
		return null;
	}
	
	public String getRefreshToken()
	{
		Object token = Authentication.getAuthenticationResult();
		if (token != null && token instanceof AzureToken)
		{
			return ((AzureToken) token).getRefreshToken();
		}
		return null;
	}
	
	public JWTClaimsSet getClaimSet()
	{
		try {
			return getIDToken().getJWTClaimsSet();
		} catch (Exception e) {
			return null;
		}
	}
	
	public Map<String, Object> getClaims()
	{
		try {
			return getClaimSet().getClaims();
		} catch (Exception e) {
			return Collections.emptyMap();
		}
	}
	
	/**
	 * Returns a 
	 * {@link #setUser(Subject, Object)}.
	 *
	 * @return the current authentication result or <code>null</code> if no user
	 *         is currently registered.
	 */	
	public Object getClaim(String claimName)
	{
		try {
			return getClaimSet().getClaim(claimName);
		} catch (Exception e) {
			return null;
		}
	}
	
	/**
	 * Returns the result of the last authentication.
	 *
	 * @return the current authentication result or <code>null</code> if no user
	 *         is currently registered.
	 */	
	public Object getAuthenticationResult()
	{
		return Authentication.getAuthenticationResult();
		
	}

	
}
