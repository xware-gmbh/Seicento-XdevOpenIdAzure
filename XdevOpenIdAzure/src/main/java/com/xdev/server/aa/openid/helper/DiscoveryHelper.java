package com.xdev.server.aa.openid.helper;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Scanner;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.servlet.ServletContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.openid.connect.sdk.op.OIDCProviderMetadata;
import com.xdev.server.aa.openid.azure.AzureBaseConfig;

public class DiscoveryHelper {

	private static final Logger log = LoggerFactory.getLogger(DiscoveryHelper.class);

	private DiscoveryHelper() {
	}


	public static OIDCProviderMetadata getProviderInfo(String endpoint)
			throws URISyntaxException, IOException, ParseException {

		URL issuerURI = new URL(endpoint + ".well-known/openid-configuration");

		InputStream stream = issuerURI.openStream();

		// Read all data from URL
		String providerInfo = null;
		
		Scanner s = new Scanner(stream);
		providerInfo = s.useDelimiter("\\A").hasNext() ? s.next() : "";
		
		s.close();

		OIDCProviderMetadata providerMetadata = OIDCProviderMetadata.parse(providerInfo);
		
		return providerMetadata;
	}
	
	public static void performDiscovery(ServletContext context) throws Throwable
	{
		ExecutorService service = null;
		try {
			service = Executors.newFixedThreadPool(1);

	        Future<Void> future = service.submit(new Callable<Void>() {

				@Override
				public Void call() throws Exception {
					AzureBaseConfig conf = ConfigHelper.getAzureBaseConfig(context);
					String endpoint = normalize(conf.getTokenBaseURL()) + normalize(conf.getTenantid());
					
					if (conf.getDiscoveryResult() == null)
					{
						OIDCProviderMetadata metadata = getProviderInfo(endpoint);
						conf.setDiscoveryResult(metadata);
						
						ConfigHelper.updateAzureBaseConfig(context, conf);
					}
					return null;
				}
			});
	        
	        future.get();
	        
		} catch (Exception e) {
			log.error("Discovery faliled", e);
			e.printStackTrace();
			throw e.getCause();
		} finally {
			service.shutdown();
		}
	}
		
	private static String normalize(final String input)
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
