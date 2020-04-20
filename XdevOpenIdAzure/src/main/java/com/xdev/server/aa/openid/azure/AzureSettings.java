package com.xdev.server.aa.openid.azure;

public enum AzureSettings {
	
    /** The tenant GUID or domain. */
    TENANT_ID("tenantid"),
    
    /** The client id for the client application. */
    CLIENT_ID("clientid"),
    
    /** The client secret for the service principal. */
    CLIENT_KEY("clientkey"),
    
	/** The callback url for the client application. */
	CLIENT_CALLBACK_URL("callbackURL"),    
    
    /** The base token URL to the current Azure environment. */
    TOKEN_BASE_URL("tokenBaseURL"),
    
    /** The URL to Active Directory Graph. */
    GRAPH_URL("graphURL"),
    
    /** The management endpoint. */
    MANAGEMENT_URL("managementURI"),    

    /** The subscription GUID. */
    SUBSCRIPTION_ID("subscriptionid"),    
    
	/** The user name for the Organization Id account. */
	ORG_USER_NAME("organisationUsername"),
	
	/** The password for the Organization Id account. */
	ORG_USER_PASS("organisationPassword"),
	
	/** Name of the default azure properties file. */
	DEFAULT_AZUREOAUTH_PROPERTIES_FILE("azureoauth.properties");
	

    /** The name of the key in the properties file. */
    private final String name;

    AzureSettings(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return this.name;
    }
}
