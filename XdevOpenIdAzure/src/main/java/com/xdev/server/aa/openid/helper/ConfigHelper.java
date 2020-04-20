package com.xdev.server.aa.openid.helper;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Optional;
import java.util.Properties;
import java.util.UUID;

import javax.servlet.ServletContext;

import org.atmosphere.util.ServletContextFactory;

import com.vaadin.server.VaadinSession;
import com.xdev.server.aa.openid.azure.AzureBaseConfig;
import com.xdev.server.aa.openid.azure.AzureSettings;

public class ConfigHelper {

	public static final String CONTEXT_OPENIDCONFIG_PARAM = "baseOpenIdConfig";
	
	private ConfigHelper() {
	}

	public static String getPathFor(String filename) {
		return ServletContextFactory.getDefault().getServletContext().getRealPath(filename);
	}

	public static String getNewState() {
		String state = UUID.randomUUID().toString();
		StoreHelper.saveInSessionStore(VaadinSession.getCurrent(), "currentState", state);
		return state;
	}
	
	public static Optional<String> getCurrentState() {
		Object state = StoreHelper.getParamFromSessionStore(VaadinSession.getCurrent(), "currentState");
		if (state != null)
		{
			return Optional.of((String) state);
		}
		return Optional.empty();
	}
	

	public static Optional<String> getCurrentNonce() {
		Object nonce = StoreHelper.getParamFromSessionStore(VaadinSession.getCurrent(), "currentNonce");
		if (nonce != null)
		{
			return Optional.of((String) nonce);
		}
		return Optional.empty();
	}	
	
	
	public static String getNewNonce() {
		return getNewNonce(32);
	}

	public static String getNewNonce(final int byteLength) {
		if (byteLength < 1) {
			throw new IllegalArgumentException("The byte length must be a positive integer");
		}

		byte[] n = new byte[byteLength];
		new SecureRandom().nextBytes(n);

		String nonce = encodeBase64(n);

		StoreHelper.saveInSessionStore(VaadinSession.getCurrent(), "currentNonce", nonce);

		return nonce;
	}

	protected static String encodeBase64(final byte[] binaryData) {
		return new String(Base64.getEncoder().encode(binaryData), StandardCharsets.UTF_8);
	}
	
	
	public static AzureBaseConfig getAzureBaseConfig(ServletContext context, boolean saveAppKeyInContext)
	{
		return getAzureBaseConfig(context, AzureSettings.DEFAULT_AZUREOAUTH_PROPERTIES_FILE.toString(), saveAppKeyInContext);
	}	
	
	public static AzureBaseConfig getAzureBaseConfig(ServletContext context)
	{
		return getAzureBaseConfig(context, AzureSettings.DEFAULT_AZUREOAUTH_PROPERTIES_FILE.toString(), true);
	}
	
	public static AzureBaseConfig getAzureBaseConfig(ServletContext context, String azureConfigFileName, boolean saveAppKeyInContext)
	{
		Object openidConfig = context.getAttribute(CONTEXT_OPENIDCONFIG_PARAM);

		if (openidConfig != null && openidConfig instanceof AzureBaseConfig) {
			return (AzureBaseConfig) openidConfig;
		}

		Properties prop = StoreHelper.readConfigFile(context, azureConfigFileName);
		AzureBaseConfig baseconfig = createAzureBaseConfig(context, prop, saveAppKeyInContext);
		baseconfig.setPropertyFileLocation(azureConfigFileName);
		return baseconfig;
	}	
	
	public static AzureBaseConfig createAzureBaseConfig(ServletContext context, File azureConfigFile) {
		
		// doesn't check if there is an existing AzureBaseConfig
		
		Properties prop = StoreHelper.readConfigFile(azureConfigFile);
		
		AzureBaseConfig baseconfig = createAzureBaseConfig(context, prop, true);
		baseconfig.setPropertyFileLocation(azureConfigFile);
		return baseconfig;
		
//		if (newBaseConfig != null)
//		{
//			updateAzureBaseConfig(context, newBaseConfig);
//			return newBaseConfig;
//		}
//		return null;
		
	}
	
	public static AzureBaseConfig createAzureBaseConfig(ServletContext context, Path azureConfigPath) {
		
		// doesn't check if there is an existing AzureBaseConfig
		
		Properties prop = StoreHelper.readConfigFile(azureConfigPath);
		AzureBaseConfig baseconfig = createAzureBaseConfig(context, prop, true);
		baseconfig.setPropertyFileLocation(azureConfigPath);
		return baseconfig;
	}

	public static AzureBaseConfig createAzureBaseConfig(ServletContext context, Properties prop, boolean saveAppKeyInContext)
	{
		if (prop == null)
		{
			return null;
		}
		final String tenantid = prop.getProperty(AzureSettings.TENANT_ID.toString(), "common");
		final String clientid = prop.getProperty(AzureSettings.CLIENT_ID.toString());
		final String clientkey = prop.getProperty(AzureSettings.CLIENT_KEY.toString());
		final String baseurl = prop.getProperty(AzureSettings.TOKEN_BASE_URL.toString(), "https://login.microsoftonline.com/");
		final String callbackurl = prop.getProperty(AzureSettings.CLIENT_CALLBACK_URL.toString());
		
		if (clientid != null && clientkey != null)
		{
			AzureBaseConfig newBaseConfig = new AzureBaseConfig(tenantid, clientid, clientkey, baseurl, saveAppKeyInContext);
			newBaseConfig.setCallbackURL(callbackurl);
			if (saveAppKeyInContext == false)
			{
				prop.remove(AzureSettings.CLIENT_KEY.toString());
			}
			newBaseConfig.setRawProperties(prop);
			updateAzureBaseConfig(context, newBaseConfig);
			return newBaseConfig;
		}
		
		return null;
	}
	
	public static void updateAzureBaseConfig(ServletContext context, AzureBaseConfig config)
	{
		context.setAttribute(CONTEXT_OPENIDCONFIG_PARAM, config);
	}
	
	public static void refreshAzureBaseConfig(ServletContext context)
	{
		
		//TODO
		context.getAttribute(CONTEXT_OPENIDCONFIG_PARAM);
	}



}
