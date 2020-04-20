package com.xdev.server.aa.openid.ui;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vaadin.addon.oauthpopup.OAuthListener;
import org.vaadin.addon.oauthpopup.OAuthPopupButton;
import org.vaadin.addon.oauthpopup.OAuthPopupConfig;

import com.github.scribejava.core.builder.api.DefaultApi10a;
import com.github.scribejava.core.builder.api.DefaultApi20;
import com.github.scribejava.core.model.Token;
import com.vaadin.ui.Notification;
import com.xdev.security.authentication.ui.Authentication;
import com.xdev.server.aa.openid.auth.AzureUser;
import com.xdev.server.aa.openid.azure.AzureIDTokenValidator;
import com.xdev.server.aa.openid.azure.AzurePopupConfig;
import com.xdev.server.aa.openid.azure.AzureToken;

/**
 * Button performs authentication with a Azure AD.
 *
 * @author XDEV Software (JM)
 *
 */
public class AzureOauthPopupButton extends OAuthPopupButton {

	private static final long serialVersionUID = 1L;
	private static final Logger log = LoggerFactory.getLogger(AzureOauthPopupButton.class);
	
	public AzureOauthPopupButton(AzurePopupConfig config) {
		super(config.createApi(), config);
		initButton();
	}	
	
	public AzureOauthPopupButton(AzurePopupConfig config, boolean withDefaultAuthenticationListener) {
		super(config.createApi(), config);
		
		if (withDefaultAuthenticationListener == true)
		{
			this.addOAuthListener(createDefaultListener());
		}
		initButton();
		
	}	
	
	public AzureOauthPopupButton(DefaultApi10a api, OAuthPopupConfig config) {
		super(api, config);
		initButton();
	}

	public AzureOauthPopupButton(DefaultApi20 api, OAuthPopupConfig config) {
		super(api, config);
		initButton();
	}

	public AzureOauthPopupButton(DefaultApi10a api, String apiKey, String apiSecret) {
		super(api, apiKey, apiSecret);
		initButton();
	}

	public AzureOauthPopupButton(DefaultApi20 api, String apiKey, String apiSecret) {
		super(api, apiKey, apiSecret);
		initButton();
	}
	
	
	
	
	public void addListener(OAuthListener listener)
	{
		if (listener != null)
		{
			this.addOAuthListener(listener);
		}
	}
	
	
	protected OAuthListener createDefaultListener()
	{
		return new OAuthListener() {

		    @Override
		    public void authSuccessful(final Token token, final boolean isOAuth20) {
		    	
		    	try {
//		    		AzureIDTokenValidator
		    		if (token instanceof AzureToken && ((AzureToken) token).getIdToken() != null) {
		    			
		    			new AzureIDTokenValidator().validateAzureIDToken((AzureToken) token);
		    			
		    			AzureUser user = new AzureUser(((AzureToken) token).getIdToken());

				    	getUI().access(new Runnable() {
							@Override
							public void run() {
								Authentication.login(user, token);
							}
						});
		    		}
		    	}
		    	catch (Exception e)
		    	{
		    		log.error("Failed to authenticate!", e);
		    		e.printStackTrace();
		    		authDenied("");
		    	}
		    }

		    @Override
		    public void authDenied(final String reason) {
		        Notification.show("Failed to authenticate!", Notification.Type.ERROR_MESSAGE);
		    }
		};
	}
	
	protected void initButton()
	{
		this.setCaption("Login");
		this.setStyleName("friendly");
	}

}
