package com.xdev.server.aa.openid.azure;

import com.xdev.server.aa.openid.OpenIDConfig;
import com.xdev.server.aa.openid.helper.StoreHelper;
import com.xdev.server.aa.openid.ui.AzureOauthPopupButton;

/**
 * Base configuration for the {@link AzureOauthPopupButton}.
 * Created automatically with Infos from a property file (Default: azureoauth.properties).
 *
 * @author XDEV Software (JM)
 *
 */
public class AzureBaseConfig extends OpenIDConfig {

	private boolean saveAppKeyInContext = true;
	private Object propertyFileLocation;
	
	public AzureBaseConfig(String tenantid, String clientid, String clientkey, String tokenBaseURL) {
		super(tenantid, clientid, clientkey, tokenBaseURL);
	}
	
	public AzureBaseConfig(String tenantid, String clientid, String clientkey, String tokenBaseURL, boolean saveAppKeyInContext) {
		super(tenantid, clientid, clientkey, tokenBaseURL);
		
		this.saveAppKeyInContext = saveAppKeyInContext;
		
		if (saveAppKeyInContext == false)
		{
			this.setClientkey(null);
		}
	}

	@Override
	public String getClientkey() {
		if (saveAppKeyInContext == false && propertyFileLocation != null)
		{
			return StoreHelper.readConfigFile(propertyFileLocation).getProperty(AzureSettings.CLIENT_KEY.toString());
		}
		return super.getClientkey();
	}

	public Object getPropertyFileLocation() {
		return propertyFileLocation;
	}

	public void setPropertyFileLocation(Object propertyFileLocation) {
		this.propertyFileLocation = propertyFileLocation;
	}

	
	
}
