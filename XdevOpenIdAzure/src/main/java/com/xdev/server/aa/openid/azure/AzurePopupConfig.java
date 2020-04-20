package com.xdev.server.aa.openid.azure;

import java.io.File;
import java.net.URI;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;

import javax.servlet.ServletContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vaadin.addon.oauthpopup.OAuthCallbackInjector;
import org.vaadin.addon.oauthpopup.OAuthPopupConfig;

import com.github.scribejava.core.model.SignatureType;
import com.vaadin.server.VaadinServlet;
import com.xdev.server.aa.openid.helper.ConfigHelper;
import com.xdev.server.aa.openid.ui.AzureOauthPopupButton;

/**
 * Configuration for the {@link AzureOauthPopupButton}. Some configuration
 * parameter are imported from an {@link AzureBaseConfig} class stored in
 * {@link ServletContext}
 *
 * @author XDEV Software (JM)
 *
 */
public class AzurePopupConfig extends OAuthPopupConfig {
	private static final Logger log = LoggerFactory.getLogger(AzurePopupConfig.class);

	private static final String DEFAULT_BASE_URI = "https://login.microsoftonline.com/";
	private static final String DEFAULT_GRAPH_URI = "https://graph.microsoft.com/";
	private static final String DEFAULT_MANAGEMENT_URI = "https://management.core.windows.net/";
	public static final String CONTEXT_CONFIG_PROPERTY = "azureconfig";
	protected static final String DEFAULT_SCOPE_V2 = "openid email profile offline_access";

	private Set<String> scopes = new HashSet<>();

	private String tenantID = "common";
	private String tokenBaseURL;
	private String graphURL;
	private String managementURL;
	private String subscriptionid;
	private String orgUsername;
	private String orgPassword;

	// private String nonce;
	private Boolean useAzureVersion2 = false;

	protected AzurePopupConfig(String apiKey, String apiSecret) {
		super(apiKey, apiSecret);
		this.tokenBaseURL = DEFAULT_BASE_URI;
		this.graphURL = DEFAULT_GRAPH_URI;
		this.managementURL = DEFAULT_MANAGEMENT_URI;
		this.setScope(DEFAULT_SCOPE_V2);
	}

	/**
	 * <p>
	 * Get {@code AzurePopupConfig} instance from ServletContext if exists.
	 * <p>
	 * <p>
	 * If not, create a new pre-configured {@code AzurePopupConfig} instance and
	 * store it in the ServletContext.
	 * <p>
	 * 
	 * @return A Azure configuration object.
	 */
	public static AzurePopupConfig getAzurePopupConfig(boolean saveAppKeyInContext) {

		AzureBaseConfig baseConfig = ConfigHelper.getAzureBaseConfig(VaadinServlet.getCurrent().getServletContext(),
				saveAppKeyInContext);
		AzurePopupConfig config = getAzurePopupConfig(baseConfig);

		return config;
	}

	/**
	 * <p>
	 * Get {@code AzurePopupConfig} instance from ServletContext if exists.
	 * <p>
	 * <p>
	 * If not, create a new pre-configured {@code AzurePopupConfig} instance and
	 * store it in the ServletContext.
	 * <p>
	 * 
	 * @return A Azure configuration object.
	 */
	public static AzurePopupConfig getAzurePopupConfig() {

		AzureBaseConfig baseConfig = ConfigHelper.getAzureBaseConfig(VaadinServlet.getCurrent().getServletContext());
		AzurePopupConfig config = getAzurePopupConfig(baseConfig);

		return config;
	}

	/**
	 * <p>
	 * Get {@code AzurePopupConfig} instance from ServletContext if exists.
	 * <p>
	 * <p>
	 * If not, create a new pre-configured {@code AzurePopupConfig} instance and
	 * store it in the ServletContext.
	 * <p>
	 * 
	 * @param callback
	 *            dynamic callback URI provided by Application
	 * @return A Azure configuration object.
	 */
	public static AzurePopupConfig getAzurePopupConfig(URI callback) {

		AzureBaseConfig baseConfig = ConfigHelper.getAzureBaseConfig(VaadinServlet.getCurrent().getServletContext());
		baseConfig.setCallbackURL(callback.toString());
		AzurePopupConfig config = getAzurePopupConfig(baseConfig);

		return config;
	}
	
	/**
	 * <p>
	 * Create a pre-configured {@code AzurePopupConfig} instance with standard
	 * Azure settings.
	 * <p>
	 * 
	 * @param propertiesFileName
	 *            The name of the azure properties file, placed in /WEB-INF
	 *            Folder.
	 * @return A pre-configured Azure configuration object.
	 */
	public static AzurePopupConfig getAzurePopupConfig(String propertiesFileName) {

		
		AzureBaseConfig baseConfig = ConfigHelper.getAzureBaseConfig(VaadinServlet.getCurrent().getServletContext(),
				propertiesFileName, true);
		AzurePopupConfig config = getAzurePopupConfig(baseConfig);

		return config;
	}

	/**
	 * <p>
	 * Create a pre-configured {@code AzurePopupConfig} instance with standard
	 * Azure settings.
	 * <p>
	 * 
	 * @param azureConfigFile
	 *            The azure properties file.
	 * @return A pre-configured Azure configuration object.
	 */
	public static AzurePopupConfig getAzurePopupConfig(File azureConfigFile) {
		AzureBaseConfig baseConfig = ConfigHelper.createAzureBaseConfig(VaadinServlet.getCurrent().getServletContext(),
				azureConfigFile);
		return getAzurePopupConfig(baseConfig);
	}

	/**
	 * <p>
	 * Create a pre-configured {@code AzurePopupConfig} instance with standard
	 * Azure settings.
	 * <p>
	 * 
	 * @param azureConfigFile
	 *            The azure properties file.
	 * @return A pre-configured Azure configuration object.
	 */
	public static AzurePopupConfig getAzurePopupConfig(Path azureConfigPath) {
		AzureBaseConfig baseConfig = ConfigHelper.createAzureBaseConfig(VaadinServlet.getCurrent().getServletContext(),
				azureConfigPath);
		return getAzurePopupConfig(baseConfig);
	}

	/**
	 * <p>
	 * Create a pre-configured {@code AzurePopupConfig} instance with standard
	 * Azure settings.
	 * <p>
	 * 
	 * @param azureSettings
	 *            The azure config properties.
	 * @return A pre-configured Azure configuration object.
	 */
	public static AzurePopupConfig getAzurePopupConfig(Properties azureSettings) {
		AzureBaseConfig baseConfig = ConfigHelper.createAzureBaseConfig(VaadinServlet.getCurrent().getServletContext(),
				azureSettings, true);
		// AzurePopupConfig config = getAzurePopupConfig(
		// azureSettings.getProperty(AzureSettings.CLIENT_ID.toString()),
		// azureSettings.getProperty(AzureSettings.CLIENT_KEY.toString()));
		//
		// config.apply(azureSettings);

		return getAzurePopupConfig(baseConfig);
	}

	/**
	 * <p>
	 * Create a pre-configured {@code AzurePopupConfig} instance with standard
	 * Azure settings.
	 * <p>
	 * 
	 * @param azureSettings
	 *            The azure config properties.
	 * @return A pre-configured Azure configuration object.
	 */
	public static AzurePopupConfig getAzurePopupConfig(AzureBaseConfig baseConfig) {
		AzurePopupConfig config = getAzurePopupConfig(baseConfig.getClientid(), baseConfig.getClientkey());

		config.apply(baseConfig.getRawProperties());

		return config;
	}

	/**
	 * <p>
	 * Create a pre-configured {@code AzurePopupConfig} instance with standard
	 * OAuth 2.0 settings.
	 * <p>
	 * 
	 * @param apiKey
	 *            The client API key for the OAuth service.
	 * @param apiSecret
	 *            The client API secret for the OAuth service.
	 * @return A pre-configured OAuth configuration object.
	 */
	public static AzurePopupConfig getAzurePopupConfig(String apiKey, String apiSecret) {
		AzurePopupConfig config = new AzurePopupConfig(apiKey, apiSecret);

		// Verursacht Probleme bei mehrfachen Logins in einer Session
		// config.setState(ConfigHelper.getNewState());

		config.setCallbackParameterName("redirect_uri").setVerifierParameterName("code").setResponseType("code")
				.setSignatureType(SignatureType.QueryString).setErrorParameterName("error");

		config.setCallbackInjector(new OAuthCallbackInjector.OAuth2StateInjector(config));
		return config;
	}

	// Azure Version
	public Boolean isAzureV2() {
		return this.useAzureVersion2;
	}

	/**
	 * Do not use! Azure v2.0 not supported at the moment.
	 * 
	 * @param useV2
	 * @return
	 */
	public AzurePopupConfig withAzureV2(final boolean useV2) {

		// this.useAzureVersion2 = useV2;
		log.warn("Azure v2.0 not supported at the moment");
		return this;
	}

	// Tenant
	public AzurePopupConfig setTenant(String tenant) {
		this.tenantID = tenant;
		return this;
	}

	public String getTenant() {
		return tenantID;
	}

	// // Nonce
	// public String getNonce() {
	// if (this.nonce == null)
	// {
	// setNonce();
	// }
	// return this.nonce;
	// }
	//
	//
	// public AzurePopupConfig setNonce() {
	// this.nonce = ConfigHelper.getNewNonce();
	// return this;
	// }

	// Scope
	@Override
	public String getScope() {
		return scopes.stream().collect(Collectors.joining(" "));
	}

	@Override
	public OAuthPopupConfig setScope(String scope) {

		if (scope != null) {
			scopes.clear();
			Arrays.stream(scope.split(" ")).filter(p -> !p.isEmpty()).forEach(q -> scopes.add(q));
		}

		return this;
	}

	public AzurePopupConfig addScopePart(String scope) {
		scopes.add(scope);
		return this;
	}

	public AzurePopupConfig removeScopePart(String scope) {
		scopes.remove(scope);
		return this;
	}

	// TokenURL
	public String getTokenBaseURL() {
		return tokenBaseURL;
	}

	public AzurePopupConfig setTokenBaseURL(String tokenBaseURL) {
		this.tokenBaseURL = tokenBaseURL;
		return this;
	}

	// GraphURL
	public String getGraphURL() {
		return graphURL;
	}

	public AzurePopupConfig setGraphURL(String graphURL) {
		this.graphURL = graphURL;
		return this;
	}

	// ManagementURL
	public String getManagementURL() {
		return managementURL;
	}

	public AzurePopupConfig setManagementURL(String managementURL) {
		this.managementURL = managementURL;
		return this;
	}

	// Subscription
	// UNUSED
	public String getSubscriptionid() {
		return subscriptionid;
	}

	public AzurePopupConfig setSubscriptionid(String subscriptionid) {
		this.subscriptionid = subscriptionid;
		return this;
	}

	// Oprganization username
	// UNUSED
	public String getOrgUsername() {
		return orgUsername;
	}

	public AzurePopupConfig setOrgUsername(String orgUsername) {
		this.orgUsername = orgUsername;
		return this;
	}

	// Oprganization password
	// UNUSED
	public String getOrgPassword() {
		return orgPassword;
	}

	public AzurePopupConfig setOrgPassword(String orgPassword) {
		this.orgPassword = orgPassword;
		return this;
	}

	protected void apply(Properties properties) {
		if (properties.getProperty(AzureSettings.CLIENT_CALLBACK_URL.toString()) != null) {
			this.setCallbackUrl(properties.getProperty(AzureSettings.CLIENT_CALLBACK_URL.toString()));
		}
		if (properties.getProperty(AzureSettings.TENANT_ID.toString()) != null) {
			this.setTenant(properties.getProperty(AzureSettings.TENANT_ID.toString()));
		}
		if (properties.getProperty(AzureSettings.TOKEN_BASE_URL.toString()) != null) {
			this.setTokenBaseURL(properties.getProperty(AzureSettings.TOKEN_BASE_URL.toString()));
		}
		if (properties.getProperty(AzureSettings.GRAPH_URL.toString()) != null) {
			this.setGraphURL(properties.getProperty(AzureSettings.GRAPH_URL.toString()));
		}
		if (properties.getProperty(AzureSettings.MANAGEMENT_URL.toString()) != null) {
			this.setManagementURL(properties.getProperty(AzureSettings.MANAGEMENT_URL.toString()));
		}
		if (properties.getProperty(AzureSettings.SUBSCRIPTION_ID.toString()) != null) {
			this.setSubscriptionid(properties.getProperty(AzureSettings.SUBSCRIPTION_ID.toString()));
		}
		if (properties.getProperty(AzureSettings.ORG_USER_NAME.toString()) != null) {
			this.setOrgUsername(properties.getProperty(AzureSettings.ORG_USER_NAME.toString()));
		}
		if (properties.getProperty(AzureSettings.ORG_USER_PASS.toString()) != null) {
			this.setOrgPassword(properties.getProperty(AzureSettings.ORG_USER_PASS.toString()));
		}
	}

	public AzureApi20 createApi() {

		return new AzureApi20().withAzureV2(isAzureV2()).withTenantID(getTenant()).withTokenURL(getTokenBaseURL())
				.withGraphURL(getGraphURL()).withManagementURL(getManagementURL());
	}

}
